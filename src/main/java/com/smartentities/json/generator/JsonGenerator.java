package com.smartentities.json.generator;

import org.json.JSONObject;

public class JsonGenerator {

	private GeneratorConfig generatorConfig;

	public JsonGenerator(GeneratorConfig generatorConfig) {
		this.generatorConfig = generatorConfig;
	}

	public JSONObject generate() {
		return (JSONObject) GeneratorFactory.getGenerator(generatorConfig.schema).generate();
	}
}