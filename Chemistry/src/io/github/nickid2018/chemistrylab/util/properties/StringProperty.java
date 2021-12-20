package io.github.nickid2018.chemistrylab.util.properties;

public class StringProperty extends Property<String> {

    private String val;

    @Override
    public String getValue() {
        return val;
    }

    @Override
    public StringProperty setValue(String s) {
        val = s;
        return this;
    }

    @Override
    public void parseStringAsObject(String input) {
        val = input;
    }
}
