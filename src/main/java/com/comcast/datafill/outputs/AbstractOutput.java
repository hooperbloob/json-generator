package com.comcast.datafill.outputs;

import com.comcast.datafill.RecordOutput;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOutput implements RecordOutput {
    protected final PathBuilder outBuilder;
    protected final int indentFactor;

    public AbstractOutput(PathBuilder pathBuilder, int theIndent) {
        outBuilder = pathBuilder;
        indentFactor = theIndent;
    }

    @Override
    public Map<String, Number> getEstimates(Collection<JSONObject> jsons) {

        long minSize = Long.MAX_VALUE;
        long maxSize = Long.MIN_VALUE;
        long totalSize = 0;
        float avgSize;

        for (JSONObject json : jsons) {
            String jsonStr = json.toString(indentFactor);
            long size = jsonStr.length();
            totalSize += size;

            minSize = Math.min(minSize, size);
            maxSize = Math.max(maxSize, size);
        }

        Map results = new HashMap<String, Number>(3);
        results.put("min", minSize);
        results.put("max", maxSize);
        results.put("avg", totalSize / jsons.size());
        return results;
    }
}
