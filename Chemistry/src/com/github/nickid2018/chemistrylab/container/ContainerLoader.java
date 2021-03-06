package com.github.nickid2018.chemistrylab.container;

import java.util.*;
import java.lang.reflect.*;
import org.apache.log4j.*;
import com.alibaba.fastjson.*;
import org.apache.commons.io.*;
import com.github.nickid2018.chemistrylab.resource.*;

public class ContainerLoader {

	public static final ArrayList<String> containers = new ArrayList<>();
	public static final Logger logger = Logger.getLogger("Container Loader");

	private final Map<String, Constructor<? extends AbstractContainer>> mapping = new HashMap<>();
	private final Map<String, TreeMap<String, Size>> sizes = new TreeMap<>();
//	private final Map<Class<?>, RangeTexture[]> tex_layers = new HashMap<>();

	public void loadContainer() {
//		long lastTime = TimeUtils.getTime();
		int fails = 0;
		for (int i = 0; i < containers.size(); i++) {
			String res = "assets/models/containers/" + containers.get(i) + ".json";
			try {
				String all = IOUtils.toString(ResourceManager.getResourceAsStream(res), "GB2312");
				JSONObject obj = JSON.parseObject(all);
				Class<?> cls = Class.forName(obj.getString("class"));
				@SuppressWarnings("unchecked")
				Constructor<? extends AbstractContainer> con = (Constructor<? extends AbstractContainer>) cls
						.getConstructor(int.class, int.class, Size.class);
				mapping.put("container." + containers.get(i) + ".model", con);
				JSONArray layer_arr = obj.getJSONArray("layers");
//				RangeTexture[] layers = new RangeTexture[layer_arr.size()];
				for (int j = 0; j < layer_arr.size(); j++) {
//					String rp = layer_arr.getString(j);
//					String[] layer_info = rp.split(",");
//					float x0 = Float.parseFloat(layer_info[1]);
//					float y0 = Float.parseFloat(layer_info[2]);
//					float x1 = Float.parseFloat(layer_info[3]);
//					float y1 = Float.parseFloat(layer_info[4]);
//					layers[j] = new RangeTexture(TextureLoader.getTexture("PNG",
//							ResourceManager.getResourceAsStream(layer_info[0]), GL_LINEAR), x0, y0, x1, y1);
				}
//				tex_layers.put(cls, layers);
				JSONArray array = obj.getJSONArray("sizes");
				TreeMap<String, Size> sss = new TreeMap<>();
				for (int j = 0; j < array.size(); j++) {
					JSONObject o = array.getJSONObject(j);
					Size s = new Size();
					s.name = o.getString("name");
					s.volume = o.getIntValue("volume");
					s.diameter = o.getIntValue("diameter");
					s.height = o.getIntValue("height");
					sss.put(s.name, s);
				}
				sizes.put("container." + containers.get(i) + ".model", sss);
			} catch (Exception e) {
				logger.warn("Can't load container " + containers.get(i));
				fails++;
			}
		}
		if (fails == 0) {
			logger.info("Successfully loaded all containers.");
		} else {
			logger.warn("Successfully loaded all containers.But " + fails + " loading failed.");
		}
	}

	public AbstractContainer newContainer(String model, int x0, int y0, Size s) throws Exception {
		return mapping.get(model).newInstance(x0, y0, s);
	}

	public Map<String, Size> getSizes(String model) {
		return sizes.get(model);
	}

//	public RangeTexture[] getLayers() {
//		Class<?> cls = ClassUtils.getCallerClass();
//		return tex_layers.get(cls);
//	}

	static {
		containers.add("beaker");
		containers.add("reagent_bottle");
		containers.add("conical_flask");
	}
}
