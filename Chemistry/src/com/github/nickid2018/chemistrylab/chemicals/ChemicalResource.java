package com.github.nickid2018.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.function.*;
import com.alibaba.fastjson.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.reaction.data.ChemicalItem;
import com.github.nickid2018.chemistrylab.reaction.data.ChemicalState;
import com.github.nickid2018.chemistrylab.reaction.data.Environment;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.mod.imc.*;

public class ChemicalResource implements IConflictable<ChemicalResource> {

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
	protected RedirectableObject<ChemicalResource> redirect = new RedirectableObject<>(this);

	public static final ChemicalResource NULL = new ChemicalResource("<null>", "<null>");

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

	public ChemicalResource merge(ChemicalResource resource) {
		ChemicalResource merged = new ChemicalResource("<merge>", "<merge>");
		merged.name = name;
		merged.boiling = boiling;
		merged.cas = cas;
		merged.clazz.putAll(clazz);
		merged.clazz.putAll(((ChemicalResource) resource).clazz);
		merged.melting = melting;
		merged.reacts.addAll(reacts);
		merged.reacts.addAll(((ChemicalResource) resource).reacts);
		merged.type = type;
		merged.unlocalizedName = unlocalizedName;
		setRedirectableObject(merged);
		resource.setRedirectableObject(merged);
		return merged;
	}

	@Override
	public ChemicalResource mergeAll(ChemicalResource... conflicts) {
		ChemicalResource resource = this;
		for (ChemicalResource conflict : conflicts) {
			resource = resource.merge(conflict);
		}
		return resource;
	}

	@Override
	public RedirectableObject<ChemicalResource> getRedirectableObject() {
		return redirect;
	}

	@Override
	public void setRedirectableObject(ChemicalResource conflict) {
		redirect.setObject(conflict);
	}

	@Override
	public void disposeRedirectable() {
		redirect = null;
	}

	@Override
	public boolean canRedirect() {
		return redirect != null;
	}

	public void doOnRedirect() {
		for (Chemical attrib : clazz.values()) {
			attrib.doOnRedirect();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ChemicalResource) && ((ChemicalResource) obj).name.equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
