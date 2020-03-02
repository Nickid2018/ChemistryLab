package com.chemistrylab.chemicals;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.init.*;
import com.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.Window;
import com.chemistrylab.render.*;

public class ChemicalsLoader {

	// Mapping of constructors of Chemical decompiling class
	public static final Map<String, Constructor<?>> mapping = new HashMap<>();
	public static final Chemicals chemicals = new Chemicals();

	// Registry
	public static final Set<String> atoms = new TreeSet<>();
	public static final Set<String> ions = new TreeSet<>();
	public static final Set<String> chemical = new TreeSet<>();

	// Logger
	public static final Logger logger = Logger.getLogger("Chemical Loader");

	// Counters
	private static int failedClasses = 0;
	private static long lastTime = ChemistryLab.getTime();
	private static int counter = 0;

	// Status to paint
	private static final String[] CHEMICAL_LOAD_STATUS = { "Loading chemical resolver(1/4)", "Loading atoms(2/4)",
			"Loading ions(3/4)", "Loading chemicals(4/4)" };

	// Progress Bar
	private static ProgressBar load_chemical_status = new ProgressBar(4, 20);
	private static ProgressBar load_details = new ProgressBar(-1, 20);

	// Load Chemical Decompilers and resource
	public static final void loadChemicals() throws Exception {
		Properties pro = new Properties();
		InputStream is = ResourceManager.getResourceAsStream("assets/models/chemicals/class.map", true);
		pro.load(is);
		load_details.setMax(pro.size());
		pro.forEach((n, c) -> {
			String ns = (String) n;
			try {
				Class<?> cls = Class.forName((String) c);
				Constructor<?> con = cls.getConstructor(JSONObject.class, ChemicalResource.class);
				mapping.put(ns, con);
			} catch (Exception e) {
				logger.warn("Failed to load TYPE CLASS:" + ns + "(Class name:" + c + ").");
				failedClasses++;
			}
			counter++;
			// Show Time!
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.QUAD.render();
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(1);
				load_details.setNow(counter);
				load_details.render(100, 520, Window.nowWidth - 200);
				CommonRender.drawAsciiFont("Loading type class:" + ns, 100, 503, 16, Color.black);
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		});

		loadAtoms();
		loadIons();
		loadChemical();

		if (failedClasses == 0 && chemicals.getFailedPartLoad() == 0) {
			logger.info("Successfully loaded all chemicals.");
		} else {
			logger.warn("Successfully loaded all chemicals.But failed to load " + failedClasses + " type(s),so "
					+ chemicals.getFailedPartLoad() + " chemical(s) loaded partly.");
		}
	}

	private static final void loadAtoms() throws Exception {
		counter = 0;
		load_details.setMax(atoms.size());
		for (String path : atoms) {
			String actualpath = "assets/models/chemicals/atoms/" + path + ".json";
			chemicals.put(path, new ChemicalResource(actualpath, path).preInit());
			counter++;
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.QUAD.render();
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(2);
				load_details.setNow(counter);
				load_details.render(100, 520, Window.nowWidth - 200);
				CommonRender.drawAsciiFont("Loading Atom[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		}
	}

	private static final void loadIons() throws Exception {
		counter = 0;
		load_details.setMax(ions.size());
		for (String path : ions) {
			String actualpath = "assets/models/chemicals/ions/" + path + ".json";
			chemicals.put(path, new ChemicalResource(actualpath, path).preInit());
			counter++;
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.QUAD.render();
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(3);
				load_details.setNow(counter);
				load_details.render(100, 520, Window.nowWidth - 200);
				CommonRender.drawAsciiFont("Loading Ion[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		}
	}

	private static final void loadChemical() throws Exception {
		counter = 0;
		load_details.setMax(chemical.size());
		for (String path : chemical) {
			String[] sps = path.split("/");
			String actualpath = "assets/models/chemicals/" + path + ".json";
			chemicals.put(sps[sps.length - 1], new ChemicalResource(actualpath, sps[sps.length - 1]).preInit());
			counter++;
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.QUAD.render();
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(3);
				renderChemicalStatus(4);
				load_details.setNow(counter);
				load_details.render(100, 520, Window.nowWidth - 200);
				CommonRender.drawAsciiFont("Loading Chemical[" + actualpath + "]", 100, 503, 16, Color.black);
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		}
	}

	private static final void renderChemicalStatus(int i) {
		load_chemical_status.setNow(i);
		load_chemical_status.render(100, 460, Window.nowWidth - 200);
		CommonRender.drawAsciiFont(CHEMICAL_LOAD_STATUS[i - 1], 100, 443, 16, Color.black);
	}

	static {
		// Atoms
		atoms.add("H");
		atoms.add("O");
		atoms.add("Na");
		// Ions
		ions.add("H_1p");
		ions.add("OH_1n");
		ions.add("Na_1p");
		// Chemicals
		chemical.add("alkalis/NaOH");
		chemical.add("oxides/H2O");
	}
}
