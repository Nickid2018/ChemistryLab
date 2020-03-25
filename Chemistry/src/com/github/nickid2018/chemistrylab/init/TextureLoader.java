package com.github.nickid2018.chemistrylab.init;

import java.io.*;
import java.util.*;
import org.lwjgl.glfw.*;
import javax.imageio.*;
import org.apache.log4j.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.window.*;
import com.github.mmc1234.minigoldengine.texture.*;
import com.github.mmc1234.minigoldengine.resource.*;
import com.github.nickid2018.chemistrylab.util.ResourceManager;

public class TextureLoader {

	public static final Logger logger = Logger.getLogger("Texture Loader");

	private static Textures textureMap;
	private static Map<String, AnimationTexture> aniTextureMap;
	private static long lastTime;
	private static long last_time;
	private static ProgressBar load_text = new ProgressBar(-1, 20);

	public static final List<String[]> guitexts = new ArrayList<>();
	public static final List<String[]> guianitexts = new ArrayList<>();

	public static void loadTexture() throws Throwable {
		// Initialize texture container
		textureMap = new Textures();
		aniTextureMap = new HashMap<>();

		// Logo
		Texture logo = getTexture("assets/textures/gui/chemistrylab_logo.png");
		textureMap.put("logo.chemistry.lab", logo);
		Texture indev = getTexture("assets/textures/gui/indev.png");
		textureMap.put("logo.chemistry.indev", indev);

		// Loading
		AnimationTexture loading = new AnimationTexture("assets/textures/gui/loading").startToBind(null);
		aniTextureMap.put("texture.guianimation.loading", loading);

		// Load GUI Textures
		logger.info("Loading GUI Textures...");
		load_text.setMax(guitexts.size() + guianitexts.size());
		last_time = TimeUtils.getTime();
		for (int i = 0; i < guitexts.size() + guianitexts.size(); i++) {
			String[] res = null;
			if (i < guitexts.size()) {
				res = guitexts.get(i);
				Texture texture = getTexture("assets/textures/gui/" + res[0] + ".png");
				textureMap.put(res[1], texture);
			} else {
				res = guianitexts.get(i - guitexts.size());
				AnimationTexture texture = new AnimationTexture("assets/textures/gui/" + res[0]);
				aniTextureMap.put(res[1], texture);
			}
			if (TimeUtils.getTime() - lastTime > 20) {
//				Window.clearFace();
//				ChemistryLab.QUAD.render();
//				load_text.setNow(i + 1);
//				load_text.render(100, 460, Window.nowWidth - 200);
//				CommonRender.showMemoryUsed();
//				InitLoader.showAllProgress(2);
//				CommonRender.drawAsciiFont("Loading GUI textures[assets/textures/gui/" + res[0] + ".png]", 100, 443, 16,
//						Color.black);
//				lastTime = ChemistryLab.getTime();
//				Window.flush();
			}
		}
		logger.info("Successfully loaded UI Textures.Used " + (TimeUtils.getTime() - last_time) + " milliseconds.");
	}

	public static void reloadTexture() throws Throwable {

		GLFW.glfwSetWindowTitle(MainWindow.window, "Reloading Textures, please wait a minute");

		// Initialize texture container
		textureMap.releaseAll();
		textureMap.clear();
		aniTextureMap.forEach((s, t) -> t.release());
		aniTextureMap.clear();

//		CommonRender.asciifont = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
//				ResourceManager.getResourceAsStream("assets/textures/font/ascii.png"), GL_NEAREST);
//		textureMap.put("font.ascii.page", CommonRender.asciifont);
//
//		Window.clearFace();
//		ChemistryLab.QUAD.render();
//		InitLoader.showReloadProgress();
//		CommonRender.showMemoryUsed();
//		Window.flush();
//
		loadTexture();
//		GLFW.glfwSetWindowTitle(Window.window, I18N.getString("window.title"));
	}

	public static final void drawLogo() {
//		if (textureMap == null)
//			return;
//		Texture logo = textureMap.get("logo.chemistry.lab");
//		if (logo == null)
//			return;
//		CommonRender.drawTexture(logo, 150, 200, 910, 390, 0, 0, 1, 0.25f);
//		Texture indev = textureMap.get("logo.chemistry.indev");
//		if (indev == null)
//			return;
//		CommonRender.drawTexture(indev, 846, 175, 974, 303, 0, 0, 1, 1);
	}

	public static Textures getTextures() {
		return textureMap;
	}

	public static Map<String, AnimationTexture> getAniTextures() {
		return aniTextureMap;
	}

	public static Texture getTexture(String path) throws IOException {
		return new Texture(new ImageInfo(ImageIO.read(ResourceManager.getResourceAsStream(path)), path));
	}

	static {
		guitexts.add(new String[] { "table", "texture.background.table" });
		guitexts.add(new String[] { "expand_bar", "texture.bar.expand" });
		guianitexts.add(new String[] { "dealing", "texture.guianimation.dealing" });
	}
}
