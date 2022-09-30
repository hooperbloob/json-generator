package com.comcast.datafill.paths;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;

/**
 * @author bremed200
 */
public class StaticPathBuilder extends AbstractPathBuilder {

    private final Path path;

    public StaticPathBuilder(Path thePath, String theSuffix) {
        super(theSuffix);
        path = thePath;
    }

    @Override
    public Path pathFor(JSONObject json, long index) {

        return path.resolve( filenameFor(json, index) );
    }

    @Override
    public Path pathFor(JSONArray array, long index) {

        JSONObject reference = (JSONObject)array.get(0);
        return pathFor(reference, index);
    }
}
