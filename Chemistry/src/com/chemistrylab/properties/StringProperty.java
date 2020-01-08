package com.chemistrylab.properties;

public class StringProperty extends Property<String> {

	private String val;

	public StringProperty setValue(String s) {
		val = s;
		return this;
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
