package com.smartentities.json.generator.generators;

import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.Schema;

public class NumberGenerator extends AbstractBoundedGenerator<Number> {

	private static long safeLongFrom(Number num, long defalt) {
		return num == null ? defalt : num.longValue();
	}

	public NumberGenerator(Schema schema) {
		super(schema);
	}

	@Override
	protected void getBounds() {

		if (schema instanceof NumberSchema) {
			NumberSchema numSchema = (NumberSchema) schema;
			min = safeLongFrom(numSchema.getMinimum(), 1);
			max = safeLongFrom(numSchema.getMaximum(), 1);
		}
	}

	@Override
	public Number generate() {

		return randomIndex();
	}
}