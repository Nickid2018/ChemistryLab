package com.github.nickid2018.chemistrylab.init;

import java.util.*;
import org.hyperic.sigar.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.mod.imc.*;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class LoadingWorld {

//	public SimpleProgressBar memorybar;
//	public Text2D textmemory;
	public LoadingWindowProgress progresses;
	public LoadingWindowProgress.ProgressEntry process;
	public ChemicalLoader chemical_loader;

	public TextureRegistry textureRegistry = new TextureRegistry("Root Registry");

//	public static AssetManager manager;

	private LoadingStatus status = LoadingStatus.START;

	/**
	 * Why this is volatile? Because it is about thread-safe problem.
	 */
	private volatile boolean lastOperationOver = false;

	private Runnable doInPreRender;
	private Runnable doInPostRender;

	private static final Map<LoadingStatus, Runnable> OPERATIONS = new HashMap<>();

	public LoadingWorld() {

		// Mod Finding
		if (Boolean.valueOf(ProgramOptions.getCommandSwitch("-modEnable", "true")))
			ModController.findMods();

		// Sigar
		loadSigarLibrary();

		// Preload Progress Bar Texture
		preloadProgressBarStyle();

		// Preload Logo
//		engine.manager.load(NameMapping.mapName("gui.chemistrylab_logo.texture"), Texture.class);
//		engine.manager.load(NameMapping.mapName("gui.indev.texture"), Texture.class);
//		engine.manager.finishLoading();

		// Register Loaders
//		engine.manager.setLoader(ChemicalResource.class,
//				chemical_loader = new ChemicalLoader(new InternalFileHandleResolver()));
//
//		setClearColor(255, 255, 255, 255);

//		progresses = new LoadingWindowProgress();

		// Fill in progress operations
		fillInOperations();

//		memorybar = new SimpleProgressBar(ChemistryLab.RUNTIME.maxMemory(), UIStyles.PROGRESSBAR_BACKGROUND,
//				UIStyles.PROGRESSBAR_FOREGROUND);
//		memorybar.setPosition(100, 100);
//		memorybar.setSize(1080, 28);
//		box.add(memorybar);

//		textmemory = new Text2D(UIStyles.FONT);
//		textmemory.setColor(Color.BLACK);
//		textmemory.getInfo().setSize(23);
//		textmemory.setPosition(100, 70);
//		textmemory.setColor(0, 0, 0, 255);
//		box.add(textmemory);

//		TextureRegion logo_region = new TextureRegion(
//				engine.manager.get(NameMapping.mapName("gui.chemistrylab_logo.texture")), 512, 128);
//		logo_region.flip(false, true);
//		Image2D logo = new Image2D(logo_region, box.camera2D);
//		logo.setPosition(158, 150);
//		logo.setSize(1024, 256);
//		box.add(logo);

		process = progresses.push(LoadingStatus.values().length);

		// Run in ThreadManager (Concurrent Thread)
		ThreadManager.invoke(this::doOnPreInit);
	}

	public void preRender(float delta) {
		trySwapNextStatus();
		double heap = MathHelper
				.eplison((ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory()) / 1048576.0, 1);
		double max = MathHelper.eplison(ChemistryLab.RUNTIME.maxMemory() / 1048576.0, 1);
		double alloc = MathHelper.eplison(ChemistryLab.RUNTIME.totalMemory() / 1048576.0, 1);
//		memorybar.setCurrent(ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory());
//		textmemory.getInfo().setText("Memory Heap " + heap + "MB / " + max + "MB (Allocated: " + alloc + "MB)");
		if (doInPreRender != null) {
			doInPreRender.run();
		}
	}

	public void postRender(float delta) {
		if (doInPostRender != null) {
			doInPostRender.run();
		}
	}

	private void preloadProgressBarStyle() {
//		engine.manager.load(NameMapping.mapName("gui.progressbar.texture"), Texture.class);
//		engine.manager.finishLoading();
//		Texture progressbar = engine.manager.get(NameMapping.mapName("gui.progressbar.texture"));
//		UIStyles.PROGRESSBAR_BACKGROUND = new TextureRegion(progressbar, 0, 0, 128, 16);
//		UIStyles.PROGRESSBAR_FOREGROUND = new TextureRegion(progressbar, 0, 16, 128, 16);
//		UIStyles.PROGRESSBAR_DIS_BACKGROUND = new TextureRegion(progressbar, 0, 32, 128, 16);
//		UIStyles.PROGRESSBAR_DIS_FOREGROUND = new TextureRegion(progressbar, 0, 48, 128, 16);
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
		OPERATIONS.put(LoadingStatus.IMC_PROCESS, this::doModIMCProcess);
		OPERATIONS.put(LoadingStatus.POST_INIT, this::doOnPostInit);
		OPERATIONS.put(LoadingStatus.FINISHING, this::doOnFinishing);
	}

	private boolean statusOver = false;

	// Render Thread
	private void trySwapNextStatus() {
		// THREAD-SAFE!!!!!!!!!!!!!!
		if (lastOperationOver) {
			if (status != LoadingStatus.FINISHING) {
				status = LoadingStatus.values()[status.ordinal() + 1];
//				process.progress.setCurrent(status.ordinal() + 1);
//				process.message.getInfo().setText(status.status);
				lastOperationOver = false;
				ThreadManager.invoke(OPERATIONS.get(status));
			} else {
				if (!statusOver) {
					// Release STRONG REFERENCE
					OPERATIONS.clear();
					progresses.pop();
					process = null;
					statusOver = true;
					textureRegistry = null;
					// To Next World
//					engine.setWorld(new ReactionTableWorld(engine));
//					doInPostRender = this::dispose;
				}
			}
		}
	}

	// Concurrent Thread
	private void doOnPreInit() {
		status = LoadingStatus.PRE_INIT;
//		process.progress.setCurrent(status.ordinal() + 1);
//		process.message.getInfo().setText(status.status);
		EngineTextureRegisterer.registerGUI(textureRegistry);
		// Mod PreInit!
		ModController.sendPreInit(textureRegistry, progresses);
		textureRegistry.lock();
		lastOperationOver = true;
	}

	private byte[] lock = new byte[0];

	// Concurrent Thread
	private void doOnInitTexture() {
//		textureRegistry.doInit(engine.manager);
		LoadingWindowProgress.ProgressEntry allProgress = progresses.push(textureRegistry.getTotalSize());
		LoadingWindowProgress.ProgressEntry detailProgress = progresses.push(1);
		doInPreRender = () -> {
//			int nowProgress = textureRegistry.getTotalSize() - engine.manager.getQueuedAssets();
//			allProgress.progress.setCurrent(nowProgress);
//			allProgress.message.getInfo()
//					.setText("Loading Textures (" + nowProgress + "/" + textureRegistry.getTotalSize() + ")");
//			TextureRegistry.ProgressInfo info = textureRegistry.getProgress(nowProgress);
//			detailProgress.progress.setMax(info.all);
//			detailProgress.progress.setCurrent(info.progress);
//			detailProgress.message.getInfo().setText(info.name + " (" + info.progress + "/" + info.all + ")");
//			if (engine.manager.update()) {
//				synchronized (lock) {
//					lock.notifyAll();
//				}
//				doInPreRender = null;
//			}
		};
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
			}
		}
		progresses.pop();
		progresses.pop();
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doModInit() {
		ModController.sendInit(ChemicalLoader.CHEMICAL_REGISTRY, progresses);
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doChemistryInit() {
		LoadingWindowProgress.ProgressEntry allProgress = progresses.push(3);
//		allProgress.message.getInfo().setText("Registering Chemical Decompilers (1/3)");
//		allProgress.progress.setCurrent(1);
		ChemicalLoader.DECOMPILER_REGISTRY.convertConstructor();
//		allProgress.message.getInfo().setText("Loading chemicals (2/3)");
//		allProgress.progress.setCurrent(2);
//		ChemicalLoader.CHEMICAL_REGISTRY.doInit(engine.manager);
		LoadingWindowProgress.ProgressEntry detailProgress = progresses
				.push(ChemicalLoader.CHEMICAL_REGISTRY.getTotalSize());
		doInPreRender = () -> {
//			int now = ChemicalLoader.CHEMICAL_REGISTRY.getTotalSize() - engine.manager.getQueuedAssets();
//			Pair<String, String> nowLoading = ChemicalLoader.CHEMICAL_REGISTRY.getNowLoading(now);
//			detailProgress.progress.setCurrent(now + 1);
//			detailProgress.message.getInfo().setText(nowLoading.key + "[" + nowLoading.value + "]" + " (" + (now + 1)
//					+ "/" + ChemicalLoader.CHEMICAL_REGISTRY.getTotalSize() + ")");
		};
//		engine.manager.finishLoading();
		doInPreRender = null;
		progresses.pop();
		progresses.pop();
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doModIMCEnqueue() {
		chemical_loader.nowChemical = null;
		ModController.sendIMCEnqueue(progresses);
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doModIMCProcess() {
		ModIMCController.imcProcess(progresses);
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doOnPostInit() {
		lastOperationOver = true;
	}

	// Concurrent Thread
	private void doOnFinishing() {
		lastOperationOver = true;
	}
}
