package com.chemistrylab.layer.container;

import java.util.*;
import java.lang.reflect.*;
import org.lwjgl.opengl.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.init.*;
import org.apache.commons.io.*;
import com.chemistrylab.render.*;

public class ContainerLoader {

	public static final ArrayList<String> containers = new ArrayList<>();
	private final Map<String, Constructor<? extends AbstractContainer>> mapping = new HashMap<>();
	private final Map<String, TreeMap<String, Size>> sizes = new TreeMap<>();
	private ProgressBar load_con = new ProgressBar(containers.size(), 20);

	public void loadContainer() throws Throwable {
		long lastTime = ChemistryLab.getTime();
		for (int i = 0; i < containers.size(); i++) {
			String res = "/assets/models/containers/" + containers.get(i) + ".json";
			String all = IOUtils.toString(ContainerLoader.class.getResourceAsStream(res), "GB2312");
			JSONObject obj = JSON.parseObject(all);
			Class<?> cls = Class.forName(obj.getString("class"));
			@SuppressWarnings("unchecked")
			Constructor<? extends AbstractContainer> con = (Constructor<? extends AbstractContainer>) cls
					.getConstructor(int.class, int.class, Size.class);
			mapping.put("container." + containers.get(i) + ".model", con);
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
	}

	public AbstractContainer newContainer(String model, int x0, int y0, Size s) throws Exception {
		return mapping.get(model).newInstance(x0, y0, s);
	}

	public Map<String, Size> getSizes(String model) {
		return sizes.get(model);
	}

	static {
		containers.add("beaker");
	}
}
