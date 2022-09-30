package com.smartentities.json.generator.generators;

import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.Schema;

import java.util.List;

public class EnumGenerator extends JsonValueGenerator<Object> {

    private final List<Object> possibleValues;

    public EnumGenerator(Schema schema) {
        super(schema);

        possibleValues = ((EnumSchema) schema).getPossibleValuesAsList();
    }

    @Override
    public Object generate() {

        int idx = random.nextInt(possibleValues.size() - 1);
        return possibleValues.get(idx);
    }
}
