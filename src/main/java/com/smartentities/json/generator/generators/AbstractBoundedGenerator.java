package com.smartentities.json.generator.generators;

import org.everit.json.schema.Schema;

/**
 * @author bremed200
 * @param <T>
 */
public abstract class AbstractBoundedGenerator<T> extends JsonValueGenerator<T>{

    protected Long min;
    protected Long max;

    protected AbstractBoundedGenerator(Schema schema) {
        super(schema);
        getBounds();
    }

    abstract void getBounds();

    protected int defaultIndex() {
        return 1;
    }

    protected long randomIndex() {

       if (max == null || min == null) return defaultIndex();

       long span = max.longValue() - min.longValue();

       long rand = (long)(span * random.nextFloat());

       return min.longValue() + rand;
    }
}
