package com.chemistrylab.properties;

public final class BooleanProperty extends Property<Boolean> {

	private boolean val;

	public BooleanProperty setValue(Boolean b) {
		val = b;
		return this;
	}

	@Override
	public Boolean getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val = Boolean.getBoolean(input);
	}

}
