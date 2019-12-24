package com.chemistrylab.init;

import org.apache.log4j.*;
import org.newdawn.slick.*;
import com.chemistrylab.render.*;
import com.chemistrylab.chemicals.*;
import com.chemistrylab.reaction.Environment;

public class InitLoader {
	
	public static final Logger logger = Logger.getLogger("Initialize Manager");
	
	private static final String[] STATUS_MAP={
			"Preparing to load",
			"Preparing Font Texture",
			"Preparing GUI Texture",
			"Loading chemicals"
	};
	private static ProgressBar all_progress=new ProgressBar(3,20);
	
	private static TextureLoader textureloader;

	public static void init() throws Throwable {
		
		//Realize loaders
		textureloader=new TextureLoader();
		
		//Start load
		logger.info("Loading textures...");
		textureloader.loadTexture();
		logger.info("Loading Chemicals...");
		ChemicalsLoader.loadChemicals();
		Environment.init();
		logger.info("Loading I18N settings...");
		I18N.load();
		MathHelper.init();
		logger.info("Program Load Over.");
	}
	
	public static void showAllProgress(int status){
		all_progress.setNow(status);
		all_progress.render(100, 400, 800);
		CommonRender.drawAsciiFont(STATUS_MAP[status], 100, 383,16, Color.black);
		TextureLoader.drawLogo();
	}
	
	public static TextureLoader getTextureLoader(){
		return textureloader;
	}
}
