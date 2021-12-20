package io.github.nickid2018.chemistrylab.util.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class Property<T> {

    public Property() {
    }

    public static Map<String, Property<?>> getProperties(Properties pro, PropertyReader reader) {
        Map<String, Property<?>> map = new HashMap<>();
        Set<String> keys = pro.stringPropertyNames();
        for (String key : keys) {
            Property<?> def = reader.getNextProperty(key);
            def.parseStringAsObject(pro.getProperty(key));
            map.put(key, def);
        }
        return map;
    }

    public abstract T getValue();

    public abstract Property<?> setValue(T t);

    public abstract void parseStringAsObject(String input);
}
