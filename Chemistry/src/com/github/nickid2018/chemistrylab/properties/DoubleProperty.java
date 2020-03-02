package com.github.nickid2018.chemistrylab.properties;

public class DoubleProperty extends Property<Double> {

	private double val;

	@Override
	public DoubleProperty setValue(Double d) {
		val = d;
		return this;
	}

	@Override
	public Double getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val = Double.parseDouble(input);
	}

}
