package com.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.function.*;
import com.alibaba.fastjson.*;
import org.apache.commons.io.*;
import com.chemistrylab.reaction.*;

public class ChemicalResource {

	protected final String resourcePath;
	protected final String fname;
	protected final Map<String, Chemical> clazz = new HashMap<>();
	protected String name;
	protected String unlocalizedName;
	protected String cas;
	protected final List<Reaction> reacts = new ArrayList<>();

	public ChemicalResource(String respath, String name) {
		resourcePath = respath;
		fname = name;
	}

	ChemicalResource preInit() throws Exception {
		InputStream is = ChemicalResource.class.getResourceAsStream(resourcePath);
		String text = IOUtils.toString(is, "GB2312");
		JSONObject object = JSON.parseObject(text);
		name = object.getString("name");
		unlocalizedName = object.getString("unlocalizedName");
		cas = object.getString("cas");
		JSONArray classes = object.getJSONArray("classes");
		classes.forEach((o) -> {
			String cl = (String) o;
			Constructor<?> cls = ChemicalsLoader.mapping.get(cl);
			try {
				JSONObject obj = object.getJSONObject("type:" + cl);
				Chemical chem = (Chemical) cls.newInstance(obj, this);
				clazz.put(cl, chem);
			} catch (Exception e) {
				ChemicalsLoader.logger.warn("Chemical Load Error at " + resourcePath + " in type " + cl
						+ ((e instanceof NullPointerException) ? ",because this type wasn't loaded." : "."));
				ChemicalsLoader.chemicals.addFailed();
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

	public void addReaction(Reaction r) {
		reacts.add(r);
	}

	public void foreach(Consumer<Reaction> o) {
		reacts.forEach(o);
	}
}
