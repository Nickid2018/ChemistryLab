package com.chemistrylab.init;

import com.cj.jmcl.*;
import com.github.nickid2018.chemistrylab.Window;

import org.apache.log4j.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.util.*;
import com.chemistrylab.render.*;
import com.chemistrylab.reaction.*;
import com.chemistrylab.chemicals.*;
import com.chemistrylab.layer.container.*;

public class InitLoader {

	public static final Logger logger = Logger.getLogger("Initialize Manager");

	private static final String[] STATUS_MAP = { "Preparing to load", "Preparing Font Texture", "Preparing GUI Texture",
			"Loading Chemicals", "Loading Containers", "Loading Reactions" };
	private static ProgressBar all_progress = new ProgressBar(3, 20);

	private static TextureLoader textureloader;
	private static ContainerLoader containerloader;

	public static void init() throws Throwable {

		// Realize loaders
		textureloader = new TextureLoader();
		containerloader = new ContainerLoader();

		// Start load
		logger.info("Loading textures...");
		textureloader.loadTexture();
		logger.info("Loading Chemicals...");
		JMCL.init();
		Environment.init();
		ChemicalsLoader.loadChemicals();
		logger.info("Loading Containers...");
		containerloader.loadContainer();
		logger.info("Loading Reactions...");
		ReactionLoader.loadReaction();
		logger.info("Loading I18N settings...");
		I18N.load();
		MathHelper.init();
		logger.info("Program Load Over.");
	}

	public static void showAllProgress(int status) {
		all_progress.setNow(status);
		all_progress.render(100, 400, Window.nowWidth - 200);
		CommonRender.drawAsciiFont(STATUS_MAP[status], 100, 383, 16, Color.black);
		TextureLoader.drawLogo();
	}

	public static void showReloadProgress() {
		all_progress.setMax(4);
		all_progress.setNow(3);
		all_progress.render(100, 400, Window.nowWidth - 200);
		CommonRender.drawAsciiFont("Reloading Texture Resource", 100, 383, 16, Color.black);
		TextureLoader.drawLogo();
	}

	public static TextureLoader getTextureLoader() {
		return textureloader;
	}

	public static ContainerLoader getContainerLoader() {
		return containerloader;
	}
}
