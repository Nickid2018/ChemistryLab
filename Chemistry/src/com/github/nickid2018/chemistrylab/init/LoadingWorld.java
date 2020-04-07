package com.github.nickid2018.chemistrylab.init;

import java.util.*;
import org.hyperic.sigar.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.github.mmc1234.pinkengine.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class LoadingWorld extends World {

	public SimpleProgressBar memorybar;
	public Text2D textmemory;
	public LoadingWindowProgress progresses;

	public TextureRegistry textureRegistry = new TextureRegistry("Root Registry");

	private volatile LoadingStatus status = LoadingStatus.START;

	/**
	 * Why this is volatile? Because it is about thread-safe problem.
	 */
	private volatile boolean lastOperationOver = false;

	private static final Map<LoadingStatus, Runnable> OPERATIONS = new HashMap<>();

	public LoadingWorld(GameChemistry engine) {
		super(engine);

		// Mod Finding
		if (Boolean.valueOf(ProgramOptions.getCommandSwitch("-modEnable", "true")))
			ModController.findMods();

		// Sigar
		loadSigarLibrary();

		// Preload Progress Bar Texture
		preloadProgressBarStyle();

		// Preload Logo
		engine.manager.load(NameMapping.mapName("gui.chemistrylab_logo.texture"), Texture.class);
		engine.manager.load(NameMapping.mapName("gui.indev.texture"), Texture.class);
		engine.manager.finishLoading();

		// Register Loaders
		engine.manager.setLoader(ChemicalResource.class, new ChemicalLoader(new InternalFileHandleResolver()));

		setClearColor(255, 255, 255, 255);

		Box box = createBox();
		setBox(box);
		progresses = new LoadingWindowProgress(box);

		// Fill in progress operations
		fillInOperations();

		memorybar = new SimpleProgressBar(ChemistryLab.RUNTIME.maxMemory(), UIStyles.PROGRESSBAR_BACKGROUND,
				UIStyles.PROGRESSBAR_FOREGROUND);
		memorybar.setPosition(100, 100);
		memorybar.setSize(1080, 28);
		box.add(memorybar);

//		textmemory = box.createText2D(UIStyles.FONT);
//		textmemory.setPosition(100, 50);
//		textmemory.setColor(0, 0, 0, 255);

		TextureRegion logo_region = new TextureRegion(
				engine.manager.get(NameMapping.mapName("gui.chemistrylab_logo.texture")), 512, 128);
		logo_region.flip(false, true);
		Image2D logo = box.createGameObject(logo_region);
		logo.setPosition(158, 150);
		logo.setSize(1024, 256);

		// Run in ThreadManager (Concurrent Thread)
		ThreadManager.invoke(this::doOnPreInit);
	}

	@Override
	public void preRender(float delta) {
		super.preRender(delta);
		trySwapNextStatus();
		float heap = ChemistryLab.APPLICATION.getJavaHeap() / 1048576.0f;
		float max = ChemistryLab.RUNTIME.maxMemory() / 1048576.0f;
		memorybar.setCurrent(ChemistryLab.APPLICATION.getJavaHeap());
		// Upadte Status
	}

	private void preloadProgressBarStyle() {
		engine.manager.load(NameMapping.mapName("gui.progressbar.texture"), Texture.class);
		engine.manager.finishLoading();
		Texture progressbar = engine.manager.get(NameMapping.mapName("gui.progressbar.texture"));
		UIStyles.PROGRESSBAR_BACKGROUND = new TextureRegion(progressbar, 0, 0, 128, 16);
		UIStyles.PROGRESSBAR_FOREGROUND = new TextureRegion(progressbar, 0, 16, 128, 16);
		UIStyles.PROGRESSBAR_DIS_BACKGROUND = new TextureRegion(progressbar, 0, 32, 128, 16);
		UIStyles.PROGRESSBAR_DIS_FOREGROUND = new TextureRegion(progressbar, 0, 48, 128, 16);
	}

	private void loadSigarLibrary() {
		try {
			Sigar.load();
		} catch (SigarException e) {
			ChemistryLab.logger.error("Can't initialize sigar.", e);
		}
	}

	private void fillInOperations() {
		OPERATIONS.put(LoadingStatus.INIT_TEXTURE, this::doOnInitTexture);
		OPERATIONS.put(LoadingStatus.INIT_MOD, this::doModInit);
		OPERATIONS.put(LoadingStatus.INIT_CHEMISTRY, this::doChemistryInit);
		OPERATIONS.put(LoadingStatus.IMC_ENQUEUE, this::doModIMCEnqueue);
		OPERATIONS.put(LoadingStatus.POST_INIT, this::doOnPostInit);
		OPERATIONS.put(LoadingStatus.IMC_PROCESS, this::doModIMCProcess);
		OPERATIONS.put(LoadingStatus.FINISHING, this::doOnFinishing);
	}

	// Render Thread
	private void trySwapNextStatus() {
		// THREAD-SAFE!!!!!!!!!!!!!!
		if (lastOperationOver) {
			if (status != LoadingStatus.FINISHING) {
				// I don't know whether is right, because the variable may not use volatile...
				status = LoadingStatus.values()[status.ordinal() + 1];
				lastOperationOver = false;
				ThreadManager.invoke(OPERATIONS.get(status));
			} else {
				// Release STRONG REFERENCE
				OPERATIONS.clear();
				// To Next World
			}
		}
	}

	// Concurrent Thread
	private void doOnPreInit() {
		status = LoadingStatus.PRE_INIT;
		EngineTextureRegisterer.registerGUI(textureRegistry);
		// Mod PreInit!
		ModController.sendPreInit(textureRegistry,progresses);
		textureRegistry.lock();
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doOnInitTexture() {
		textureRegistry.doInit(engine.manager);
		lastOperationOver = true;
	}
	
	private void doModInit() {
		lastOperationOver = true;
	}
	
	private void doChemistryInit() {
		lastOperationOver = true;
	}
	
	private void doModIMCEnqueue() {
		lastOperationOver = true;
	}
	
	private void doOnPostInit() {
		lastOperationOver = true;
	}
	
	private void doModIMCProcess() {
		lastOperationOver = true;
	}
	
	private void doOnFinishing() {
		lastOperationOver = true;
	}
}
