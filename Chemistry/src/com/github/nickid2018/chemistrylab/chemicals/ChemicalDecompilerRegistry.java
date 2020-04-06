package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;
import java.lang.reflect.*;
import com.alibaba.fastjson.*;

public class ChemicalDecompilerRegistry {

	public final Map<String, Class<? extends Chemical>> REGISTER = new HashMap<>();
	public final Map<String, Constructor<?>> MAPPING = new HashMap<>();
	private int failedClasses = 0;

	public final void addChemicalDecompiler(String name, Class<? extends Chemical> clazz) {
		REGISTER.put(name, clazz);
	}

	public final void convertConstructor() {
		REGISTER.forEach((s, c) -> {
			try {
				Constructor<?> ctor = c.getConstructor(JSONObject.class, ChemicalResource.class);
				MAPPING.put(s, ctor);
			} catch (Exception e) {
				ChemicalLoader.logger.warn("Failed to load TYPE CLASS:" + s + "(Class name:" + c + ").");
				failedClasses++;
			}
		});
	}

	public final void checkLoad() {
		if (failedClasses == 0 && ChemicalLoader.CHEMICALS.getFailedPartLoad() == 0) {
			ChemicalLoader.logger.info("Successfully loaded all chemicals.");
		} else {
			ChemicalLoader.logger.warn("Successfully loaded all chemicals.But failed to load " + failedClasses
					+ " type(s),so " + ChemicalLoader.CHEMICALS.getFailedPartLoad() + " chemical(s) loaded partly.");
		}
	}
}
