package com.github.nickid2018.chemistrylab.util.properties;

public class DoubleProperty extends Property<Double> {

    private double val;

    @Override
    public Double getValue() {
        return val;
    }

    @Override
    public DoubleProperty setValue(Double d) {
        val = d;
        return this;
    }

    @Override
    public void parseStringAsObject(String input) {
        val = Double.parseDouble(input);
    }

}
