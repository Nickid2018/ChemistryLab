package com.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.lwjgl.opengl.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.init.*;
import com.chemistrylab.render.*;

public class ChemicalsLoader {

	public static final Map<String, Constructor<?>> mapping = new HashMap<>();
	public static final Chemicals chemicals = new Chemicals();
	public static final Logger logger = Logger.getLogger("Chemical Loader");
	
	private static int failedClasses = 0;
	private static long lastTime=ChemistryLab.getTime();
	private static int counter=0;
	
	private static final String[] CHEMICAL_LOAD_STATUS={
		"Loading chemical resolver(1/4)",
		"Loading atoms(2/4)",
		"Loading ions(3/4)",
		"Loading chemicals(4/4)"
	};
	
	private static ProgressBar load_chemical_status=new ProgressBar(4,20);
	private static ProgressBar load_details=new ProgressBar(-1,20);

	public static final void loadChemicals() throws Exception {
		Properties pro = new Properties();
		InputStream is = ChemicalsLoader.class.getResourceAsStream("/assets/models/chemicals/class.map");
		pro.load(is);
		load_details.setMax(pro.size());
		pro.forEach((n, c) -> {
			String ns = (String) n;
			try {
				Class<?> cls = Class.forName((String) c);
				Constructor<?> con = cls.getConstructor(JSONObject.class);
				mapping.put(ns, con);
			} catch (Exception e) {
				logger.warn("Failed to load TYPE CLASS:" + ns + "(Class name:" + c + ").");
				failedClasses++;
			}
			counter++;
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.updateFPS();
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(1);
				load_details.setNow(counter);
				load_details.render(100, 520, 800);
				CommonRender.drawAsciiFont("Loading type class:"+ns, 100, 503, 16,Color.black);
				Display.update();
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		});
		
		loadAtoms();
		loadIons();

		if (failedClasses == 0 && chemicals.getFailedPartLoad() == 0) {
			logger.info("Successfully loaded all chemicals.");
		} else {
			logger.warn("Successfully loaded all chemicals.But failed to load " + failedClasses + " type(s),so "
					+ chemicals.getFailedPartLoad() + " chemical(s) loaded partly.");
		}
	}

	private static final void loadAtoms() throws Exception {
		chemicals.put("H", new ChemicalResource("/assets/models/chemicals/atoms/H.json").preInit());
	}

	private static final void loadIons() throws Exception {
		chemicals.put("H_1p", new ChemicalResource("/assets/models/chemicals/ions/H_1p.json").preInit());
	}
	
	private static final void renderChemicalStatus(int i){
		load_chemical_status.setNow(i);
		load_chemical_status.render(100, 460, 800);
		CommonRender.drawAsciiFont(CHEMICAL_LOAD_STATUS[i-1], 100, 443, 16,Color.black);
	}
}
