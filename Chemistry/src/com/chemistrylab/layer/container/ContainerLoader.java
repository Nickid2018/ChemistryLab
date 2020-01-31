package com.chemistrylab.layer.container;

import java.util.*;
import java.lang.reflect.*;
import org.lwjgl.opengl.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.init.*;
import org.newdawn.slick.util.*;
import org.apache.commons.io.*;
import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class ContainerLoader {

	public static final ArrayList<String> containers = new ArrayList<>();
	public static final Logger logger = Logger.getLogger("Container Loader");

	private final Map<String, Constructor<? extends AbstractContainer>> mapping = new HashMap<>();
	private final Map<String, TreeMap<String, Size>> sizes = new TreeMap<>();
	private final Map<Class<?>, Texture[]> tex_layers = new HashMap<>();
	private ProgressBar load_con = new ProgressBar(containers.size(), 20);

	public void loadContainer() {
		long lastTime = ChemistryLab.getTime();
		int fails = 0;
		for (int i = 0; i < containers.size(); i++) {
			String res = "/assets/models/containers/" + containers.get(i) + ".json";
			try {
				String all = IOUtils.toString(ContainerLoader.class.getResourceAsStream(res), "GB2312");
				JSONObject obj = JSON.parseObject(all);
				Class<?> cls = Class.forName(obj.getString("class"));
				@SuppressWarnings("unchecked")
				Constructor<? extends AbstractContainer> con = (Constructor<? extends AbstractContainer>) cls
						.getConstructor(int.class, int.class, Size.class);
				mapping.put("container." + containers.get(i) + ".model", con);
				JSONArray layer_arr = obj.getJSONArray("layers");
				Texture[] layers = new Texture[layer_arr.size()];
				for (int j = 0; j < layer_arr.size(); j++) {
					String rp = layer_arr.getString(j);
					layers[j] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(rp), GL_LINEAR);
				}
				tex_layers.put(cls, layers);
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
			if (ChemistryLab.getTime() - lastTime > 20) {
				ChemistryLab.clearFace();
				ChemistryLab.updateFPS();
				load_con.setNow(i + 1);
				load_con.render(100, 460, ChemistryLab.nowWidth - 200);
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(2);
				CommonRender.drawAsciiFont("Loading Container[" + res + "]", 100, 443, 16, Color.black);
				Display.update();
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
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

	public Texture[] getLayers() {
		Class<?> cls = ChemistryLab.getCallerClass();
		return tex_layers.get(cls);
	}

	static {
		containers.add("beaker");
		containers.add("reagent_bottle");
	}
}
