package com.github.nickid2018.chemistrylab.properties;

public class IntegerProperty extends Property<Long> {

	private long val;

	@Override
	public IntegerProperty setValue(Long i) {
		val = i;
		return this;
	}

	@Override
	public Long getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val = Long.parseLong(input);
	}

}
