package com.chemistrylab.properties;

import java.util.*;
import com.cj.jmcl.*;

public class MathStatementProperty extends Property<Double> {

	private MathStatement ms;
	private double storedLast = 0;
	private Map<String, Double> values = new HashMap<>();

	public MathStatementProperty(String... varns) {
		for (String n : varns) {
			values.put(n, 0.0);
		}
	}

	public void setValue(String n, double v) {
		values.replace(n, v);
	}

	public void calc() {
		storedLast = ms.calc(values);
	}

	@Override
	public Double getValue() {
		return storedLast;
	}

	@Override
	public void parseStringAsObject(String input) throws Exception {
		ms = MathStatement.format(input);
	}

	@Override
	public MathStatementProperty setValue(Double t) {
		throw new RuntimeException();
	}

	@Override
	public String toString() {
		return ms.toString();
	}

}
