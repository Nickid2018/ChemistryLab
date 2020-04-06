package com.github.nickid2018.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.function.*;
import com.alibaba.fastjson.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.resource.ResourceManager;

public class ChemicalResource {

	public static enum ChemicalType {
		/**
		 * For a chemical whose type is atom
		 */
		ATOM,
		/**
		 * For an ion
		 */
		ION,
		/**
		 * For a simple subtance
		 */
		SIMPLE,
		/**
		 * For a simple chemical
		 */
		CHEMICAL
	}

	protected final String resourcePath;
	protected final String fname;
	protected final Map<String, Chemical> clazz = new HashMap<>();
	protected ChemicalType type;
	protected String name;
	protected String unlocalizedName;
	protected String cas;
	protected double melting = Double.MAX_VALUE;
	protected double boiling = Double.MAX_VALUE;
	protected final List<Reaction> reacts = new ArrayList<>();

	public ChemicalResource(String respath, String name) {
		resourcePath = respath;
		fname = name;
	}

	ChemicalResource preInit() throws Exception {
		InputStream is = ResourceManager.getResourceAsStream(resourcePath);
		String text = IOUtils.toString(is, "GB2312");
		JSONObject object = JSON.parseObject(text);
		name = object.getString("name");
		unlocalizedName = object.getString("unlocalizedName");
		cas = object.getString("cas");
		type = ChemicalType.valueOf(object.getString("type").toUpperCase());
		JSONArray classes = object.getJSONArray("classes");
		classes.forEach((o) -> {
			String cl = (String) o;
			Constructor<?> cls = ChemicalLoader.DECOMPILER_REGISTRY.MAPPING.get(cl);
			try {
				JSONObject obj = object.getJSONObject("type:" + cl);
				Chemical chem = (Chemical) cls.newInstance(obj, this);
				clazz.put(cl, chem);
			} catch (Exception e) {
				ChemicalLoader.logger.warn("Chemical Load Error at " + resourcePath + " in type " + cl
						+ ((e instanceof NullPointerException) ? ",because this type wasn't loaded." : "."));
				ChemicalLoader.CHEMICALS.addFailed();
			}
		});
		return this;
	}

	public String getCAS() {
		return cas;
	}

	public double getMess() {
		double ret = 0;
		for (Chemical c : clazz.values()) {
			if (c.isActualMess())
				ret = c.getMess();
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public String getFinalName() {
		return fname;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public boolean hasAttribute(String cls) {
		for (String cl : clazz.keySet()) {
			if (cl.startsWith(cls))
				return true;
		}
		return false;
	}

	public ChemicalType getType() {
		return type;
	}

	public ChemicalState getState(double temperature) {
		return temperature < melting ? ChemicalState.SOLID
				: (temperature > boiling ? ChemicalState.GAS : ChemicalState.LIQUID);
	}

	public ChemicalItem getDefaultItem() {
		return new ChemicalItem(this, getState(Environment.getTemperature()));
	}

	public void addReaction(Reaction r) {
		reacts.add(r);
	}

	public void foreach(Consumer<Reaction> o) {
		reacts.forEach(o);
	}
}
