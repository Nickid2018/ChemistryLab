package com.github.nickid2018.chemistrylab.init;

import java.util.List;
import org.hyperic.sigar.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.google.common.collect.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.github.mmc1234.pinkengine.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.github.nickid2018.chemistrylab.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.resource.*;
import com.github.nickid2018.chemistrylab.util.ThreadManager;
import com.github.nickid2018.chemistrylab.chemicals.*;

public class LoadingWorld extends World {

	public static final List<String> LOADING_STATUS = Lists.newArrayList("Preparing Mods", "Pre Initialization",
			"Initializing Program", "Mod IMC Enqueue", "Post Initialization", "Finishing Initialization");

	public ProgressBar memorybar;
	public Text2D textmemory;

	public TextureRegistry textureRegistry = new TextureRegistry("Root Registry");

	private LoadingStatus status = LoadingStatus.START;

	/**
	 * Why this is volatile? Because it is about thread-safe problem.
	 */
	private volatile boolean lastOperationOver = true;

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

		memorybar = new ProgressBar(0, ChemistryLab.RUNTIME.maxMemory() / 1048576.0f, 0.000001f, false,
				UIStyles.PROGRESS_BAR_STYLE);
		memorybar.setPosition(100, 75);
		memorybar.setSize(1080, 28);
		box.add(memorybar);

		textmemory = box.createText2D(new VectorFont(new FileHandle("C:\\Windows\\Fonts\\MSYH.TTC")));
		textmemory.setPosition(100, 50);
		textmemory.setFontSize(28);
		textmemory.setColor(0, 0, 0, 255);
		textmemory.setText("Memory Heap");

		// Run in ThreadManager (Concurrent Thread)
		ThreadManager.invoke(() -> {
			status = LoadingStatus.PRE_INIT;
			lastOperationOver = false;
			// Mod PreInit!
			ModController.sendPreInit(textureRegistry);
			textureRegistry.doInit(engine.manager);
			lastOperationOver = true;
		});
	}

	@Override
	public void preRender(float delta) {
		super.preRender(delta);
		float heap = ChemistryLab.APPLICATION.getJavaHeap() / 1048576.0f;
		float max = ChemistryLab.RUNTIME.maxMemory() / 1048576.0f;
		memorybar.setValue(heap);
		textmemory.setText("Memory Heap: " + MathHelper.eplison(heap, 1) + "MB/" + MathHelper.eplison(max, 1) + "MB");
		// Upadte Status
	}

	private void preloadProgressBarStyle() {
		engine.manager.load(NameMapping.mapName("gui.progressbar.texture"), Texture.class);
		engine.manager.finishLoading();
		Texture progressbar = engine.manager.get(NameMapping.mapName("gui.progressbar.texture"));
		TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(progressbar, 0, 0, 128, 16));
		TextureRegionDrawable foreground = new TextureRegionDrawable(new TextureRegion(progressbar, 0, 16, 128, 16));
		TextureRegionDrawable dis_back = new TextureRegionDrawable(new TextureRegion(progressbar, 0, 32, 128, 16));
		TextureRegionDrawable dis_fore = new TextureRegionDrawable(new TextureRegion(progressbar, 0, 48, 128, 16));
		UIStyles.PROGRESS_BAR_STYLE = new ProgressBar.ProgressBarStyle();
		foreground.setMinWidth(0);
		dis_fore.setMinWidth(0);
		background.setMinHeight(28);
		foreground.setMinHeight(28);
		UIStyles.PROGRESS_BAR_STYLE.background = background;
		UIStyles.PROGRESS_BAR_STYLE.knobBefore = foreground;
		UIStyles.PROGRESS_BAR_STYLE.disabledBackground = dis_back;
		UIStyles.PROGRESS_BAR_STYLE.disabledKnobBefore = dis_fore;
	}

	private void loadSigarLibrary() {
		try {
			Sigar.load();
		} catch (SigarException e) {
			ChemistryLab.logger.error("Can't initialize sigar.", e);
		}
	}

	private void doOnInit() {

	}
}
