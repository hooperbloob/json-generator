package com.comcast.datafill.paths;

import com.comcast.datafill.outputs.PathBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bremed200
 */
public class DateTimePathBuilder extends AbstractPathBuilder {

    private final Path              base;
    private final String[]          timestampPath;   // path to the timestamp field
    private final SimpleDateFormat  jsonDateFormat;  // timestamp field format
    private final SimpleDateFormat  pathFormat;

    private static final String LONG_DATE_PATTERN = "yyyy/MM/dd";

    public DateTimePathBuilder(Path theBase, String theTimestampPath, String jDateFormat) {
        this(theBase, theTimestampPath, jDateFormat, LONG_DATE_PATTERN);
    }

    public DateTimePathBuilder(Path theBase, String theTimestampPath, String jDateFormat, String pathDateFormat) {
        super(".json");

        base = theBase;
        timestampPath = theTimestampPath.split(" ");
        jsonDateFormat = new SimpleDateFormat(jDateFormat);
        pathFormat = new SimpleDateFormat(pathDateFormat);
    }

    protected Date timestampFrom(JSONObject json) {

        String tsStr = json.getString(timestampPath[0]);
        try {
            return jsonDateFormat.parse(tsStr);
        } catch (ParseException pe) {
            return null;
        }
    }

    private Path dirPathFor(Date ts) {

        String[] pth = pathFormat.format(ts).split("/");
        return Paths.get(base.toString(), pth);
    }

    @Override
    public Path pathFor(JSONObject json, long index) {

        Date ts = timestampFrom(json);
        Path fullDir = dirPathFor(ts);
        return fullDir.resolve(filenameFor(json, index));
    }

    @Override
    public Path pathFor(JSONArray array, long index) {

        JSONObject reference = (JSONObject) array.get(0);

        Date ts = timestampFrom(reference);
        Path fullDir = dirPathFor(ts);
        return fullDir.resolve(filenameFor(array, index));
    }

    /**
     * Testing....
     *
     * @param args
     */
    public static void main(String[] args) {

        final String StdDateFormat = "EEE MMM d HH:mm:ss z yyyy";

        JSONObject obj = new JSONObject();
        obj.put("title", "Snow White Gets Arrested");
        obj.put("released", new Date().toString());

        PathBuilder pb = new DateTimePathBuilder(Paths.get("/Users/bremed200/github/json-generator/results"),"released", StdDateFormat);
        Path fullPath = pb.pathFor(obj, 0);

        try {
            Files.createDirectories(fullPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(fullPath);
    }
}
