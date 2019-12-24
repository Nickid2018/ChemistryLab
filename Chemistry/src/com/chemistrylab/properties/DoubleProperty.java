package com.chemistrylab.properties;

public class DoubleProperty extends Property<Double> {
	
	private double val;

	public void setValue(double d){
		val=d;
	}
	
	@Override
	public Double getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val=Double.parseDouble(input);
	}

}
