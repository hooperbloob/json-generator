package com.comcast.datafill.outputs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;

/**
 * Creates a path based on the json object provided.
 *
 * @author bremed200
 */
public interface PathBuilder {

    Path pathFor(JSONObject json, long index);

    Path pathFor(JSONArray json, long index);
}
