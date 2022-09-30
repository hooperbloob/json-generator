package com.comcast.datafill.paths;

import com.comcast.datafill.outputs.PathBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author bremed200
 */
public abstract class AbstractPathBuilder implements PathBuilder {

    private final String fileSuffix;

    protected AbstractPathBuilder(String theFileSuffix) {
        fileSuffix = theFileSuffix;
    }

    protected String filenameFor(JSONObject json, long index) {
        return Long.toString(index) + fileSuffix;
    }

    protected String filenameFor(JSONArray array, long index) {
        return Long.toString(index) + "_" + array.length() + fileSuffix;
    }
}
