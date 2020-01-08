package com.chemistrylab.properties;

import java.util.*;

public abstract class Property<T> {

	public Property() {
	}

	public abstract Property<?> setValue(T t);

	public abstract T getValue();

	public abstract void parseStringAsObject(String input) throws Exception;

	public static final Map<String, Property<?>> getProperties(Properties pro, PropertyReader reader) throws Exception {
		Map<String, Property<?>> map = new HashMap<>();
		Set<String> keys = pro.stringPropertyNames();
		for (String key : keys) {
			Property<?> def = reader.getNextProperty(key);
			def.parseStringAsObject(pro.getProperty(key));
			map.put(key, def);
		}
		return map;
	}
}
