package com.chemistrylab.properties;

public class StringProperty extends Property<String> {

	private String val;

	public void setValue(String s) {
		val = s;
	}

	@Override
	public String getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val = input;
	}
}
