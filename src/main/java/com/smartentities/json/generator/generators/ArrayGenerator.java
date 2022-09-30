package com.smartentities.json.generator.generators;

import com.comcast.datafill.DataGenerator;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.Schema;
import org.json.JSONArray;

import com.smartentities.json.generator.GeneratorFactory;

public class ArrayGenerator extends AbstractBoundedGenerator<JSONArray> {

	public ArrayGenerator(Schema schema) {
		super(schema);
	}

	@Override
	protected void getBounds() {

		if (schema instanceof ArraySchema) {
			ArraySchema arraySchema = (ArraySchema) schema;
			min = Long.valueOf(arraySchema.getMinItems());
			max = Long.valueOf(arraySchema.getMaxItems());
		}
	}

	@Override
	public JSONArray generate() {

		if (schema instanceof ArraySchema) {
			ArraySchema arraySchema = (ArraySchema) schema;

			Schema allItemSchema = arraySchema.getAllItemSchema();

			final long count = randomIndex();

			JSONArray jsonArray = new JSONArray();

			for (int i = 0; i<count; i++) {
				jsonArray.put(
					GeneratorFactory.getGenerator(allItemSchema).generate()
					);
			}

			return jsonArray;
		}
		return new JSONArray();
	}
}