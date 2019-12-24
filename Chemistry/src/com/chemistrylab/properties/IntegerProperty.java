package com.chemistrylab.properties;

public class IntegerProperty extends Property<Long> {
	
	private long val;
	
	public void setValue(long i){
		val=i;
	}

	@Override
	public Long getValue() {
		return val;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		val=Long.parseLong(input);
	}

}
