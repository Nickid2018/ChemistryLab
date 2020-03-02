package com.github.nickid2018.chemistrylab.properties;

public final class BooleanProperty extends Property<Boolean> {

	private boolean val;

	@Override
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
