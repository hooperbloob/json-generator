package com.comcast.datafill.outputs;

import com.comcast.datafill.RecordOutput;
import com.comcast.datafill.Status;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author bremed200
 */
public class FileOutput extends AbstractOutput {

    public FileOutput(PathBuilder pathBuilder, int theIndent) {
        super(pathBuilder, theIndent);
    }

    @Override
    public void consume(JSONObject json, Status status) {

        String jsonStr = json.toString(indentFactor);
        long filesize = jsonStr.length();

        Path out = outBuilder.pathFor(json, status.recordsCreated() + 1);

        write(out, jsonStr, status);

        status.incrementRecordCount(1);
        status.addConsumed(filesize);
    }

    @Override
    public void consume(JSONArray array, Status status) {

        String jsonStr = array.toString(indentFactor);
        long filesize = jsonStr.length();

        Path out = outBuilder.pathFor(array, status.filesCreated() + 1);

        write(out, jsonStr, status);

        status.incrementRecordCount(array.length());
        status.addConsumed(filesize);
    }

    private void write(Path out, String text, Status status) {

        try {
            Files.createDirectories(out.getParent());
            Files.write(out, text.getBytes(), StandardOpenOption.CREATE);
            status.incrementFileCount(1);
            } catch (IOException ex) {
                System.err.print(ex.getMessage());
                }
    }
 }
