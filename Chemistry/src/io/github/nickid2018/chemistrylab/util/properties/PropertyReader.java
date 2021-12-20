package io.github.nickid2018.chemistrylab.util.properties;

public interface PropertyReader {

    Property<?> getNextProperty(String name);
}
