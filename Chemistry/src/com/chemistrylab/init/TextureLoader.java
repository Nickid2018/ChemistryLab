package com.chemistrylab.init;

import java.util.*;
import org.lwjgl.opengl.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import org.newdawn.slick.util.*;
import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.textures.*;

import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {

	public static final Logger logger = Logger.getLogger("Texture Loader");

	private static Textures textureMap;
	private long lastTime;
	private long last_time;
	private ProgressBar load_text = new ProgressBar(-1, 20);

	public static final List<String[]> guitexts = new ArrayList<>();
	public static final List<String[]> guianitexts = new ArrayList<>();

	void loadTexture() throws Throwable {

		// Initialize texture container
		textureMap = new Textures();
		CommonRender.init();

		ChemistryLab.clearFace();
		InitLoader.showAllProgress(0);
		CommonRender.showMemoryUsed();
		Display.update();

		// Logo
		Texture logo = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("assets/textures/gui/chemistrylab_logo.png"), GL_LINEAR);
		textureMap.put("logo.chemistry.lab", logo);
		Texture indev = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("assets/textures/gui/indev.png"), GL_LINEAR);
		textureMap.put("logo.chemistry.indev", indev);

		// Loading
		Texture loading = new AnimationTexture("assets/textures/gui/loading").startToBind();
		textureMap.put("texture.guianimation.loading", loading);

		// Load font textures
		logger.info("Loading fonts...");
		load_text.setMax(256);
		lastTime = last_time = ChemistryLab.getTime();
		Texture[] fonts = new Texture[256];
		for (int i = 0; i < 256;) {
			String suffix = Integer.toHexString(i);
			suffix = suffix.length() == 1 ? "0" + suffix : suffix;
			String resource = "assets/textures/font/unicode_page_" + suffix + ".png";
			String resource_name = "font.unicode.page" + suffix;
			try {
				Texture t = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
						ResourceLoader.getResourceAsStream(resource), GL_NEAREST);
				textureMap.put(resource_name, t);
				fonts[i] = t;
			} catch (Exception e) {
			}
			load_text.setNow(++i);
			if (ChemistryLab.getTime() - lastTime > 16) {
				ChemistryLab.clearFace();
				ChemistryLab.updateFPS();
				load_text.render(100, 460, 800);
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(1);
				CommonRender.drawAsciiFont("Loading unicode font page[" + resource + "]", 100, 443, 16, Color.black);
				Display.update();
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		}
		CommonRender.loadUnicodes(fonts);
		logger.info("Successfully loaded fonts.Used " + (ChemistryLab.getTime() - last_time) + " milliseconds.");
		
		// Load GUI Textures
		logger.info("Loading GUI Textures...");
		load_text.setMax(guitexts.size() + guianitexts.size());
		last_time = ChemistryLab.getTime();
		for (int i = 0; i < guitexts.size() + guianitexts.size(); i++) {
			Texture texture = null;
			String[] res = null;
			if (i < guitexts.size()) {
				res = guitexts.get(i);
				texture = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
						ResourceLoader.getResourceAsStream("assets/textures/gui/" + res[0] + ".png"), GL11.GL_LINEAR);
				textureMap.put(res[1], texture);
			} else {
				res = guianitexts.get(i - guitexts.size());
				texture = new AnimationTexture("assets/textures/gui/" + res[0]);
				textureMap.put(res[1], texture);
			}
			if (ChemistryLab.getTime() - lastTime > 20) {
				ChemistryLab.clearFace();
				ChemistryLab.updateFPS();
				load_text.setNow(i + 1);
				load_text.render(100, 460, 800);
				CommonRender.showMemoryUsed();
				InitLoader.showAllProgress(2);
				CommonRender.drawAsciiFont("Loading GUI textures[assets/textures/gui/" + res[0] + ".png]", 100, 443, 16,
						Color.black);
				Display.update();
				lastTime = ChemistryLab.getTime();
				ChemistryLab.flush();
			}
		}
		logger.info("Successfully loaded UI Textures.Used " + (ChemistryLab.getTime() - last_time) + " milliseconds.");
	}

	public static final void drawLogo() {
		if (textureMap == null)
			return;
		Texture logo = textureMap.get("logo.chemistry.lab");
		if (logo == null)
			return;
		CommonRender.drawTexture(logo, 150, 200, 910, 390, 0, 0, 1, 0.25f);
		Texture indev = textureMap.get("logo.chemistry.indev");
		if (indev == null)
			return;
		CommonRender.drawTexture(indev, 846, 175, 974, 303, 0, 0, 1, 1);
	}

	public Textures getTextures() {
		return textureMap;
	}

	static {
		guitexts.add(new String[] { "table", "texture.background.table" });
		guitexts.add(new String[] { "expand_bar", "texture.bar.expand" });
		guianitexts.add(new String[] { "dealing", "texture.guianimation.dealing" });
	}
}
