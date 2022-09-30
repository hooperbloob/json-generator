package com.smartentities.json.generator.generators;

import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class StringGenerator extends AbstractBoundedGenerator<String> {

	String subset = "abcdefghijklmnopqrstuvwxyz ";

	public static final String DateFormat = "yyyy-MM-dd";
	private static long[] getDateBounds(StringSchema schema) {
		Map props = schema.getUnprocessedProperties();
		String minDate = (String)props.get("minDate");
		String maxDate = (String)props.get("maxDate");
		if (minDate == null || maxDate == null) return null;

		SimpleDateFormat df = new SimpleDateFormat(DateFormat);

		try {
			return new long[]{
					df.parse(minDate).getTime(),
					df.parse(maxDate).getTime()
					};
		} catch (ParseException pe) {
			return null;
		}
	}

	private static String dateStringFor(long timestamp) {
		Date time = new Date(timestamp);
		return new SimpleDateFormat(DateFormat).format(time);
	}

	public StringGenerator(Schema schema) {
		super(schema);
	}

	private Long getMin(StringSchema schema) {
		return Long.valueOf(schema.getMinLength());
	}

	private Long getMax(StringSchema schema) {
		return Long.valueOf(schema.getMaxLength());
	}

	@Override
	protected void getBounds() {

		if (schema instanceof StringSchema) {
			StringSchema strSchema = (StringSchema) schema;
			long[] dateRange = getDateBounds(strSchema);
			if (dateRange == null) {
				min = getMin(strSchema);
				max = getMax(strSchema);
				} else {
					min = dateRange[0];
					max = dateRange[1];
				}
		}
	}

	@Override
	public String generate() {

		long length = randomIndex();

		if (length > 10000000L) {
			return dateStringFor(length);
		}

		StringBuilder sb = new StringBuilder((int)length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(subset.length());
			char c = subset.charAt(index);
			sb.append(c);
		}
		return sb.toString();
	}
}