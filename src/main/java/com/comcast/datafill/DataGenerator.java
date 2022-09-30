package com.comcast.datafill;

import com.comcast.datafill.outputs.FileOutput;
import com.comcast.datafill.outputs.PathBuilder;
import com.comcast.datafill.outputs.S3Output;
import com.comcast.datafill.paths.DateTimePathBuilder;
import com.smartentities.json.generator.GeneratorConfig;
import com.smartentities.json.generator.JsonGenerator;
import com.smartentities.json.generator.generators.StringGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * References a json schema file and generates randomized JSON files based on it up to
 * some specified limit:  elapsed time, # of records, or # of files
 *
 * Depending on the output chosen files can be sent to your local filesystem or a
 * bucket in S3.  Path/folder structures can be flat (all-in-one) or spread across
 * a number of subfolders based on a date field in the generated json file.
 *
 * Ongoing statistics are copied as an empty file to the local filesystem every 10
 * seconds. Deleting this file will cancel the generation job.
 *
 * Date values: The dateMin and dateMax fields, when provided, are used to format
 * date values and aren't part of an official JSON schema - its a hack just for here.
 *
 * Generation mode: We can generate json based on the schema as an individual record
 * or as an array of them. The limits item controls the final size of the array.
 *
 * Edit the main() method to compose your file output and path generation options
 *
 * TODO
 *  * Externalize RecordsPerFile & UpdateSeconds to config file
 *  * Migrate all changes made to the type-specific generators to new classes so they can be referenced by the original author's GeneratorConfig class.
 *  * Fork onto background thread(s) to avoid inadvertent cancellation via console IO
 *  * Develop & reference a proper configuration file to avoid polluting the cmd line args
 *  * Revise status output to display progress and time remaining based on the nearest limit value
 *  * Provide option to enable/disable pretty-printed JSON output (for more compact files)
 *
 * @author bremed200
 */
public class DataGenerator {

    private Status status;

    private GenMode mode = GenMode.Array;

    private final GeneratorConfig generatorConfig;
    private final RecordOutput output;
    private final Limits limits;
    private final JsonGenerator jsonGenerator;

    // TODO externalize in a config
    private static final int UpdateSeconds = 10;
    private static final int RecordsPerFile = 50;

    private static final int SampleRecordCount = 1000;

    public DataGenerator(GeneratorConfig genConfig, Limits theLimits, RecordOutput theOutput ) {
        generatorConfig = genConfig;
        output = theOutput;
        limits = theLimits;

        jsonGenerator = new JsonGenerator(generatorConfig);
    }

    private JSONArray generateArray() {

        JSONArray array = new JSONArray();

        for (int i=0; i<RecordsPerFile; i++) {
            array.put(jsonGenerator.generate());
        }
        return array;
    }

    public void run() {

        status = new Status(FileSystems.getDefault().getPath(""), UpdateSeconds);

        while (limits.canContinue(status)) {
            switch (mode) {
                case Single: { output.consume(jsonGenerator.generate(), status); break; }
                case  Array: { output.consume(generateArray(), status);
                }
            }
            if (!renderStatus()) {
                System.out.println("Cancelled..");
                break;
            }
        }
    }

    private boolean renderStatus() {
        try {
            return status.renderStatus();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
                }
    }

    public void showStats(PrintStream ps) {
        status.showStats(ps);
    }

    private void showEstimatesFor(int sampleCount, RecordOutput out, PrintStream ps) {

        Collection<JSONObject> jsons = new ArrayList<>(sampleCount);

        for (long i=0; i<sampleCount; i++) {
            jsons.add( jsonGenerator.generate() );
        }

        DecimalFormat df = new DecimalFormat("###,###,###");

        Map<String,Number> estimates = out.getEstimates(jsons);
        ps.println("Record size stats for " + sampleCount + " records:");

        for (Map.Entry<String,Number> entry : estimates.entrySet()) {
            ps.println("\t" + entry.getKey() + ": " + df.format(entry.getValue()));
        }

        Long avgSize = (Long)estimates.get("avg");

        if (limits.maxRecordCount != null) {
            ps.println("\nEstimated size for " + df.format(limits.maxRecordCount) + " records: " + df.format(limits.maxRecordCount * avgSize));
            }
    }

    /**
     * arg[0]  path to the local JSON schema file
     * arg[1]  target directory or S3 bucket path
     * arg[2]  limit value (files for now)
     *
     *  i.e.:  /Users/br/json-generator/src/test/resources/movie-schema.json /Users/br/json-generator/results 30
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        GeneratorConfig cfg = GeneratorConfig.fromSchemaPath(args[0]);
        Path output = Paths.get(args[1]);
        IOUtil.deleteDir(output.toFile());

        Limits limits = Limits.Builder.newInstance()
                .maxFileCount( Integer.parseInt(args[2]))
                .build();

        //     PathBuilder pathBuilder = new StaticPathBuilder(output, ".json");
        PathBuilder pathBuilder = new DateTimePathBuilder(output, "released", StringGenerator.DateFormat );

             RecordOutput out = new FileOutput(pathBuilder, 3);
       // RecordOutput out = new S3Output(pathBuilder, 3, "S3://adveng");

        DataGenerator gen = new DataGenerator(cfg, limits, out);
        gen.showEstimatesFor(SampleRecordCount, out, System.out);

        gen.run();

        gen.showStats(System.out);
    }
}
