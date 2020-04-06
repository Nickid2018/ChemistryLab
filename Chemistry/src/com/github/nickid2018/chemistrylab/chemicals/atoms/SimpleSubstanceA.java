package com.github.nickid2018.chemistrylab.chemicals.atoms;

import java.util.*;
import java.lang.reflect.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class SimpleSubstanceA extends Chemical {

	private Set<Integer> sstypes = new HashSet<>();

	public SimpleSubstanceA(JSONObject o, ChemicalResource r) {
		super(o, r);
		JSONArray datas = o.getJSONArray("data");
		for (int i = 0; i < datas.size(); i++) {
			JSONObject object = datas.getJSONObject(i);
			String name = object.getString("name");
			String unlocalizedName = object.getString("unlocalizedName");
			int count = object.getIntValue("count");
			sstypes.add(count);
			JSONArray classes = object.getJSONArray("classes");
			ChemicalResourceOfSS sb = new ChemicalResourceOfSS(r.getResourcePath(), r.getFinalName() + count, name,
					unlocalizedName, r.getCAS(), r, count);
			sb.getClazz().put("simple-substance", new SimpleSubstance(o, sb));
			classes.forEach((ob) -> {
				String cl = (String) ob;
				Constructor<?> cls = ChemicalLoader.DECOMPILER_REGISTRY.MAPPING.get(cl);
				try {
					JSONObject obj = object.getJSONObject("type:" + cl);
					Chemical chem = (Chemical) cls.newInstance(obj, sb);
					sb.getClazz().put(cl, chem);
				} catch (Exception e) {
					ChemicalLoader.logger.warn("Chemical Load Error at " + r.getResourcePath() + count + " in type "
							+ cl + ((e instanceof NullPointerException) ? ",because this type wasn't loaded." : "."));
					ChemicalLoader.CHEMICALS.addFailed();
				}
			});
			ChemicalLoader.CHEMICALS.put(r.getFinalName() + count, sb);
		}
	}

	@Override
	public double getMess() {
		return 0;
	}

	static class ChemicalResourceOfSS extends ChemicalResource {

		private final ChemicalResource superRes;
		private final int count;

		ChemicalResourceOfSS(String respath, String fname, String name, String unlocalizedName, String cas,
				ChemicalResource sup, int count) {
			super(respath, fname);
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.cas = cas;
			superRes = sup;
			this.count = count;
		}

		Map<String, Chemical> getClazz() {
			return clazz;
		}

		ChemicalResource getSuperRes() {
			return superRes;
		}

		int getCount() {
			return count;
		}
	}
}
