package com.github.nickid2018.chemistrylab.resource;

import java.util.*;

public class NameMapping {

	public static final Map<String, String> NAME_MAP = new HashMap<>();

	public static String mapName(String resourceName) {
		for (String regex : NAME_MAP.keySet()) {
			if (resourceName.matches(regex))
				return String.format(NAME_MAP.get(regex), resourceName.split("\\.")[1]);
		}
		return resourceName;
	}

	public static void addMapName(String prefix, String suffix, String pattern) {
		NAME_MAP.put(prefix + "\\.\\w+\\." + suffix, pattern);
	}

	static {
		// Default Mappings
		addMapName("gui", "texture", "assets/textures/gui/%s.png");
		
	}
}
