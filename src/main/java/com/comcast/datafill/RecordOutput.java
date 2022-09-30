package com.comcast.datafill;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author bremed200
 */
public interface RecordOutput {

    Map<String,Number> getEstimates(Collection<JSONObject> jsons);

    void consume(JSONObject json, Status status);

    void consume(JSONArray json, Status status);
}
