package com.github.nickid2018.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.log4j.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.chemicals.ions.*;
import com.github.nickid2018.chemistrylab.chemicals.alkalis.*;
import com.github.nickid2018.chemistrylab.chemicals.atoms.*;
import com.github.nickid2018.chemistrylab.chemicals.oxides.*;
import com.github.nickid2018.chemistrylab.window.MainWindow;

public class ChemicalsLoader {
	
	// Registry
	public static final Map<String, Class<?>> REGISTER = new HashMap<>();
	public static final Set<String> REGISTRY_ATOM = new TreeSet<>();
	public static final Set<String> REGISTRY_ION = new TreeSet<>();
	public static final Set<String> REGISTRY_CHEMICAL = new TreeSet<>();
	
	public static final Chemicals CHEMICALS = new Chemicals();
	
	// Mapping of constructors of Chemical decompiling class
	public static final Map<String, Constructor<?>> mapping = new HashMap<>();
	
	// Logger
	public static final Logger logger = Logger.getLogger("Chemical Loader");

	// Counters
	private static int failedClasses = 0;
	private static long lastTime = TimeUtils.getTime();
	private static int counter = 0;

	// Status to paint
	private static final String[] CHEMICAL_LOAD_STATUS = { "Loading chemical resolver(1/4)", "Loading atoms(2/4)",
			"Loading ions(3/4)", "Loading chemicals(4/4)" };

	// Progress Bar
	private static ProgressBar load_chemical_status = new ProgressBar(4, 20);
	private static ProgressBar load_details = new ProgressBar(-1, 20);

	// Load Chemical Decompilers and resource
	public static final void loadChemicals() throws Exception {

		REGISTER.forEach((s, c) -> {
			try {
				Constructor<?> ctor = c.getConstructor(JSONObject.class, ChemicalResource.class);
				mapping.put(s, ctor);
			} catch (Exception e) {
				logger.warn("Failed to load TYPE CLASS:" + s + "(Class name:" + c + ").");
				failedClasses++;
			}
		});

		loadAtoms();
		loadIons();
		loadChemical();

		if (failedClasses == 0 && CHEMICALS.getFailedPartLoad() == 0) {
			logger.info("Successfully loaded all chemicals.");
		} else {
			logger.warn("Successfully loaded all chemicals.But failed to load " + failedClasses + " type(s),so "
					+ CHEMICALS.getFailedPartLoad() + " chemical(s) loaded partly.");
		}
	}

	private static final void loadAtoms() throws Exception {
		counter = 0;
		load_details.setMax(REGISTRY_ATOM.size());
		for (String path : REGISTRY_ATOM) {
			String actualpath = "assets/models/chemicals/atoms/" + path + ".json";
			CHEMICALS.put(path, new ChemicalResource(actualpath, path).preInit());
			counter++;
			if (TimeUtils.getTime() - lastTime > 16) {
				MainWindow.clearFace();
//				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(2);
				load_details.setNow(counter);
				load_details.render(100, 520, MainWindow.nowWidth - 200);
//				CommonRender.drawAsciiFont("Loading Atom[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = TimeUtils.getTime();
				MainWindow.flush();
			}
		}
	}

	private static final void loadIons() throws Exception {
		counter = 0;
		load_details.setMax(REGISTRY_ION.size());
		for (String path : REGISTRY_ION) {
			String actualpath = "assets/models/chemicals/ions/" + path + ".json";
			CHEMICALS.put(path, new ChemicalResource(actualpath, path).preInit());
			counter++;
			if (TimeUtils.getTime() - lastTime > 16) {
				MainWindow.clearFace();
//				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(3);
				load_details.setNow(counter);
				load_details.render(100, 520, MainWindow.nowWidth - 200);
//				CommonRender.drawAsciiFont("Loading Ion[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = TimeUtils.getTime();
				MainWindow.flush();
			}
		}
	}

	private static final void loadChemical() throws Exception {
		counter = 0;
		load_details.setMax(REGISTRY_CHEMICAL.size());
		for (String path : REGISTRY_CHEMICAL) {
			String[] sps = path.split("/");
			String actualpath = "assets/models/chemicals/" + path + ".json";
			CHEMICALS.put(sps[sps.length - 1], new ChemicalResource(actualpath, sps[sps.length - 1]).preInit());
			counter++;
			if (TimeUtils.getTime() - lastTime > 16) {
				MainWindow.clearFace();
//				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(4);
				load_details.setNow(counter);
				load_details.render(100, 520, MainWindow.nowWidth - 200);
//				CommonRender.drawAsciiFont("Loading Chemical[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = TimeUtils.getTime();
				MainWindow.flush();
			}
		}
	}

	private static final void renderChemicalStatus(int i) {
		load_chemical_status.setNow(i);
		load_chemical_status.render(100, 460, MainWindow.nowWidth - 200);
//		CommonRender.drawAsciiFont(CHEMICAL_LOAD_STATUS[i - 1], 100, 443, 16, Color.black);
	}

	private static final void registerChemDecomper(String name, Class<?> cls) {
		REGISTER.put(name, cls);
	}

	static {
		registerChemDecomper("atom/nonmetal",NonMentalAtom.class);
		registerChemDecomper("atom/metal",MentalAtom.class);
		registerChemDecomper("simple",SimpleSubstanceA.class);
		registerChemDecomper("ion/H",IonH.class);
		registerChemDecomper("ion/OH",IonOH.class);
		registerChemDecomper("ion/strong",IonStrong.class);
		registerChemDecomper("alkali/strong",AlkaliStrong.class);
		registerChemDecomper("chemical/H2O",H2O.class);
		// Atoms
		REGISTRY_ATOM.add("H");
		REGISTRY_ATOM.add("O");
		REGISTRY_ATOM.add("Na");
		// Ions
		REGISTRY_ION.add("H_1p");
		REGISTRY_ION.add("OH_1n");
		REGISTRY_ION.add("Na_1p");
		// Chemicals
		REGISTRY_CHEMICAL.add("alkalis/NaOH");
		REGISTRY_CHEMICAL.add("oxides/H2O");
	}
}
