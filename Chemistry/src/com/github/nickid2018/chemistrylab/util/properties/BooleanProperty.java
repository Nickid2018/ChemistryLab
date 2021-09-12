package com.github.nickid2018.chemistrylab.util.properties;

public final class BooleanProperty extends Property<Boolean> {

    private boolean val;

    @Override
    public Boolean getValue() {
        return val;
    }

    @Override
    public BooleanProperty setValue(Boolean b) {
        val = b;
        return this;
    }

    @Override
    public void parseStringAsObject(String input) {
        val = Boolean.getBoolean(input);
    }

}
