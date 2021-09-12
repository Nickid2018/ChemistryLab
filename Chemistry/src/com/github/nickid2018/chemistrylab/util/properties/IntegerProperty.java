package com.github.nickid2018.chemistrylab.util.properties;

public class IntegerProperty extends Property<Long> {

    private long val;

    @Override
    public Long getValue() {
        return val;
    }

    @Override
    public IntegerProperty setValue(Long i) {
        val = i;
        return this;
    }

    @Override
    public void parseStringAsObject(String input) {
        val = Long.parseLong(input);
    }

}
