package com.chemistrylab.render;

import java.util.*;
import java.awt.Toolkit;
import org.lwjgl.opengl.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.Window;

import org.newdawn.slick.opengl.*;
import org.newdawn.slick.font.effects.*;

import static com.chemistrylab.ChemistryLab.*;
import static org.lwjgl.opengl.GL11.*;

public class CommonRender {

	public static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
	public static final int WINDOW_HEIGHT = TOOLKIT.getScreenSize().height;
	public static final int WINDOW_WIDTH = TOOLKIT.getScreenSize().width;
	public static final Map<Integer, UnicodeFont> LOAD_FONTS = new HashMap<>();
	public static final Map<Integer, String> ADD_STRINGS = new HashMap<>();
	public static final String FONT_NAME = "΢���ź�";
	public static final FastQuad TABLE = new FastQuad(0, 0, Window.nowWidth, Window.nowHeight, new Color(150, 150, 150, 75));

	private static Texture[] fonts;
	public static Texture asciifont;
	private static boolean font_loaded = false;
	private static final ProgressBar memory = new ProgressBar(ChemistryLab.getTotalMemory(), 20);

	public static void loadUnicodes(Texture[] fonts) {
		CommonRender.fonts = fonts;
		font_loaded = true;
	}

	@SuppressWarnings("unchecked")
	public static void reloadFontUNI() {
		Map<Integer, UnicodeFont> shadow = new HashMap<>();
		for (Integer size : LOAD_FONTS.keySet()) {
			UnicodeFont u = new UnicodeFont(new java.awt.Font(FONT_NAME, java.awt.Font.PLAIN, size));
			String get = ADD_STRINGS.get(size);
			if (!get.isEmpty()) {
				u.getEffects().add(new ColorEffect());
				u.addGlyphs(get);
				try {
					u.loadGlyphs();
				} catch (SlickException e) {
				}
			}
			shadow.put(size, u);
		}
		LOAD_FONTS.clear();
		LOAD_FONTS.putAll(shadow);
		System.gc();
	}

	public static void preLoadFontUNI(int size) {
		if (!LOAD_FONTS.containsKey(size)) {
			LOAD_FONTS.put(size, new UnicodeFont(new java.awt.Font(FONT_NAME, java.awt.Font.PLAIN, size)));
			LOAD_FONTS.get(size).setPaddingAdvanceY(0);
			ADD_STRINGS.put(size, "");
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadFontUNI(String s, int size) {
		preLoadFontUNI(size);
		LOAD_FONTS.get(size).getEffects().add(new ColorEffect());
		LOAD_FONTS.get(size).addGlyphs(s);
		ADD_STRINGS.replace(size, ADD_STRINGS.get(size) + s);
		try {
			LOAD_FONTS.get(size).loadGlyphs();
		} catch (SlickException e) {
		}
	}

	public static void drawFontUNI(String s, float start, float f, Color text, int size) {
		LOAD_FONTS.get(size).drawString(start, f, s, text);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static int getFontLengthUNI(String s, int size) {
		return LOAD_FONTS.get(size).getWidth(s);
	}

	public static int getFontHeightUNI(String s, int size) {
		return LOAD_FONTS.get(size).getHeight(s);
	}

	public static float drawFont(String s, float x, float f, int size, Color text) {
		return drawFont(s, x, f, size, text, false);
	}

	public static float drawFont(String s, float x, float next, int size, Color text, boolean withshade) {
		return drawFont(s, x, next, size, text, withshade, new Color(150, 150, 150, 75));
	}

	public static float drawFont(String s, float x, float next, float size, Color text, boolean withshade,
			Color shade) {
		float drawYS = winToOthHeight(size);
		if (s.indexOf('\n') > -1) {
			String[] all = s.split("\n");
			size = formatSize(size);
			drawYS = winToOthHeight(size);
			for (int i = 0; i < all.length; i++) {
				drawFont(all[i], x, next + i * drawYS, size, text, withshade, shade);
			}
			return -1;
		}
		if (s.indexOf('\r') < 0) {
			size = formatSize(size);
			drawYS = winToOthHeight(size);
		}
		// Replaced \r
		s = s.replaceAll("\r", "");
		float drawXS = winToOthWidth(size);
		if (!font_loaded) {
			ChemistryLab.logger.error("Are you kidding me?I haven't load font!");
			return -1;
		}
		char[] all = s.toCharArray();
		float lastx = x;
		if (withshade) {
			for (char c : all) {
				if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
					lastx += drawXS / 2;
				} else if (Character.isSpaceChar(c)) {
					lastx += drawXS / 2;
				} else if (c == '\t') {
					lastx += drawXS * 2;
				} else {
					lastx += drawXS;
				}
			}
			shade.bind();
			glBegin(GL_QUADS);
			glVertex2f(x, next);
			glVertex2f(lastx, next);
			glVertex2f(lastx, next + drawYS);
			glVertex2f(x, next + drawYS);
			glEnd();
			lastx = x;
		}
		text.bind();
		glEnable(GL_TEXTURE_2D);
		for (char c : all) {
			int at = c >> 8;
			int pagein = c & 0xFF;
			Texture t = fonts[at];
			t.bind();
			float texturex;
			float texturey;
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, next);
				glTexCoord2f(texturex + 0.03125f, texturey);
				glVertex2f(lastx + drawXS / 2, next);
				glTexCoord2f(texturex + 0.03125f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS / 2, next + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, next + drawYS);
				glEnd();
				lastx += drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx += drawXS / 2;
			} else if (c == '\t') {
				lastx += drawXS * 2;
			} else {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, next);
				glTexCoord2f(texturex + 0.0625f, texturey);
				glVertex2f(lastx + drawXS, next);
				glTexCoord2f(texturex + 0.0625f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS, next + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, next + drawYS);
				glEnd();
				lastx += drawXS;
			}
		}
		glDisable(GL_TEXTURE_2D);
		return lastx;
	}

	public static float drawSpeFont(String s, float x, float next, int size, Color text) {
		float drawYS = winToOthHeight(size) / 2;
		float lastx = x;
		char[] all = s.toCharArray();
		boolean up = false, down = false;
		int start = 0;
		for (int i = 0; i < all.length; i++) {
			char c = all[i];
			if (c == '~') {
				if (!down) {
					up = !up;
					if (up) {
						lastx = drawFont(s.substring(start, i), lastx, next, size, text);
						start = i + 1;
					} else {
						lastx = drawFont(s.substring(start, i), lastx, next, size / 2, text);
						start = i + 1;
					}
				}
			}
			if (c == '`') {
				if (!up) {
					down = !down;
					if (down) {
						lastx = drawFont(s.substring(start, i), lastx, next, size, text);
						start = i + 1;
					} else {
						lastx = drawFont(s.substring(start, i), lastx, next + drawYS, size / 2, text);
						start = i + 1;
					}
				}
			}
		}
		lastx = drawFont(s.substring(start, s.length()), lastx, next, size, text);
		return lastx;
	}

	public static void drawRightFont(String s, float x, float y, float size, Color text, boolean withshade) {
		drawRightFont(s, x, y, size, text, withshade, new Color(150, 150, 150, 75));
	}

	public static void drawRightFont(String s, float x, float y, float size, Color text, boolean withshade,
			Color shade) {
		float drawYS = winToOthHeight(size);
		if (s.indexOf('\n') > -1) {
			String[] all = s.split("\n");
			size = formatSize(size);
			drawYS = winToOthHeight(size);
			for (int i = 0; i < all.length; i++) {
				drawRightFont(all[i], x, y + i * drawYS, size, text, withshade, shade);
			}
			return;
		}
		if (s.indexOf('\r') < 0) {
			size = formatSize(size);
			drawYS = winToOthHeight(size);
		}
		// Replaced \r
		s = s.replaceAll("\r", "");
		float drawXS = winToOthWidth(size);
		if (!font_loaded) {
			ChemistryLab.logger.error("Are you kidding me?I haven't load font!");
			return;
		}
		char[] all = s.toCharArray();
		float lastx = x;
		for (char c : all) {
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				lastx -= drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx -= drawXS / 2;
			} else if (c == '\t') {
				lastx -= drawXS * 2;
			} else {
				lastx -= drawXS;
			}
		}
		if (withshade) {
			shade.bind();
			glBegin(GL_QUADS);
			glVertex2f(x, y);
			glVertex2f(lastx, y);
			glVertex2f(lastx, y + drawYS);
			glVertex2f(x, y + drawYS);
			glEnd();
		}
		text.bind();
		glEnable(GL_TEXTURE_2D);
		for (char c : all) {
			int at = c >> 8;
			int pagein = c & 0xFF;
			Texture t = fonts[at];
			t.bind();
			float texturex;
			float texturey;
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, y);
				glTexCoord2f(texturex + 0.03125f, texturey);
				glVertex2f(lastx + drawXS / 2, y);
				glTexCoord2f(texturex + 0.03125f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS / 2, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx += drawXS / 2;
			} else if (c == '\t') {
				lastx += drawXS * 2;
			} else {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, y);
				glTexCoord2f(texturex + 0.0625f, texturey);
				glVertex2f(lastx + drawXS, y);
				glTexCoord2f(texturex + 0.0625f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS;
			}
		}
		glDisable(GL_TEXTURE_2D);
	}

	public static float drawItaticFont(String s, float x, float y, float size, Color text, float shr) {
		size = formatSize(size);
		float drawXS = winToOthWidth(size);
		float drawYS = winToOthHeight(size);
		if (!font_loaded) {
			ChemistryLab.logger.error("Are you kidding me?I haven't load font!");
			return -1;
		}
		char[] all = s.toCharArray();
		text.bind();
		float lastx = x;
		glEnable(GL_TEXTURE_2D);
		for (char c : all) {
			int at = c >> 8;
			int pagein = c & 0xFF;
			Texture t = fonts[at];
			t.bind();
			float texturex;
			float texturey;
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx + drawXS * shr, y);
				glTexCoord2f(texturex + 0.03125f, texturey);
				glVertex2f(lastx + drawXS * shr + drawXS / 2, y);
				glTexCoord2f(texturex + 0.03125f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS / 2, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx += drawXS / 2;
			} else if (c == '\t') {
				lastx += drawXS * 2;
			} else {
				texturex = (pagein & 0xF) / 16.0f;
				texturey = (pagein >> 4) / 16.0f;
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx + drawXS * shr, y);
				glTexCoord2f(texturex + 0.0625f, texturey);
				glVertex2f(lastx + drawXS * shr + drawXS, y);
				glTexCoord2f(texturex + 0.0625f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS;
			}
		}
		glDisable(GL_TEXTURE_2D);
		return lastx;
	}

	public static void drawAsciiFont(String s, int x, int y, int size, Color text) {
		drawAsciiFont(s, x, y, size, text, false);
	}

	public static void drawAsciiFont(String s, int x, int y, float size, Color text, boolean withshade) {
		size = formatSize(size);
		float drawXS = winToOthWidth(size);
		float drawYS = winToOthHeight(size);
		char[] all = s.toCharArray();
		int lastx = x;
		if (withshade) {
			for (char c : all) {
				if (c == 'i' || c == 'l') {
					lastx += drawXS * 1 / 4;
				} else {
					lastx += drawXS * 3 / 4;
				}
			}
			new Color(150, 150, 150, 75).bind();
			glBegin(GL_QUADS);
			glVertex2f(x, y);
			glVertex2f(lastx, y);
			glVertex2f(lastx, y + drawYS);
			glVertex2f(x, y + drawYS);
			glEnd();
			lastx = x;
		}
		text.bind();
		glEnable(GL_TEXTURE_2D);
		asciifont.bind();
		float texturex;
		float texturey;
		for (char c : all) {
			if (c >= 255)
				continue;
			// Specials:
			texturex = (c & 0xF) / 16.0f;
			texturey = (c >> 4) / 16.0f;
			if (c == 'i' || c == 'l') {
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, y);
				glTexCoord2f(texturex + 0.0625f / 2, texturey);
				glVertex2f(lastx + drawXS * 1 / 2, y);
				glTexCoord2f(texturex + 0.0625f / 2, texturey + 0.0625f);
				glVertex2f(lastx + drawXS * 1 / 2, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS * 1 / 4;
			} else {
				glBegin(GL_QUADS);
				glTexCoord2f(texturex, texturey);
				glVertex2f(lastx, y);
				glTexCoord2f(texturex + 0.0625f, texturey);
				glVertex2f(lastx + drawXS, y);
				glTexCoord2f(texturex + 0.0625f, texturey + 0.0625f);
				glVertex2f(lastx + drawXS, y + drawYS);
				glTexCoord2f(texturex, texturey + 0.0625f);
				glVertex2f(lastx, y + drawYS);
				glEnd();
				lastx += drawXS * 3 / 4;
			}
		}
		glDisable(GL_TEXTURE_2D);
	}

	public static void drawTexture(Texture t, float x0, float y0, float x1, float y1, float sx, float sy, float sx0,
			float sy0) {
		drawTexture(t, x0, y0, x1, y1, sx, sy, sx0, sy0, Color.white);
	}

	public static void drawTexture(Texture t, float x0, float y0, float x1, float y1, float sx, float sy, float sx0,
			float sy0, Color c) {
		glEnable(GL_TEXTURE_2D);

		t.bind();
		c.bind();

		glBegin(GL_QUADS);
		glTexCoord2f(sx, sy);
		glVertex2f(x0, y0);
		glTexCoord2f(sx0, sy);
		glVertex2f(x1, y0);
		glTexCoord2f(sx0, sy0);
		glVertex2f(x1, y1);
		glTexCoord2f(sx, sy0);
		glVertex2f(x0, y1);
		glEnd();

		glDisable(GL_TEXTURE_2D);
	}

	// Old No
	// public static float winToOthWidth(float window){
	// return window*ChemistryLab.nowWidth/Display.getWidth();
	// }
	//
	// public static float winToOthHeight(float window){
	// return window*ChemistryLab.nowHeight/Display.getHeight();
	// }
	//
	// public static float othToWinWidth(float oth){
	// return oth*Display.getWidth()/(float)ChemistryLab.nowWidth;
	// }
	//
	// public static float othToWinHeight(float oth){
	// return oth*Display.getHeight()/(float)ChemistryLab.nowHeight;
	// }

	public static float winToOthWidth(float window) {
		return window;
	}

	public static float winToOthHeight(float window) {
		return window;
	}

	public static float othToWinWidth(float oth) {
		return oth;
	}

	public static float othToWinHeight(float oth) {
		return oth;
	}

	public static float calcTextWidth(String text, float size) {
		size = formatSize(size);
		float drawXS = winToOthWidth(size);
		char[] all = text.toCharArray();
		float lastx = 0;
		for (char c : all) {
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				lastx += drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx += drawXS / 2;
			} else if (c == '\t') {
				lastx += drawXS * 2;
			} else {
				lastx += drawXS;
			}
		}
		return lastx;
	}

	public static String subTextWidth(String text, float size, float limit) {
		size = formatSize(size);
		float drawXS = winToOthWidth(size);
		char[] all = text.toCharArray();
		float lastx = 0;
		int end = text.length();
		for (int i = 0; i < all.length; i++) {
			char c = all[i];
			if (c < 0x0600 && !Character.isSpaceChar(c) && !Character.isISOControl(c)) {
				lastx += drawXS / 2;
			} else if (Character.isSpaceChar(c)) {
				lastx += drawXS / 2;
			} else if (c == '\t') {
				lastx += drawXS * 2;
			} else {
				lastx += drawXS;
			}
			if (lastx >= limit) {
				end = i;
				break;
			}
		}
		return text.substring(0, end);
	}

	// Old No
	// public static float formatSize(float size){
	// int mul = Display.getWidth() / ChemistryLab.nowWidth;
	// mul = mul == 0 ? 1 : mul;
	// return size * mul;
	// }

	public static float formatSize(float size) {
		return size;
	}

	public static float toRatioXPos(float x) {
		return x / 1280 * Window.nowWidth;
	}

	public static float toRatioYPos(float y) {
		return y / 720 * Window.nowHeight;
	}

	public static float toGLX(float x) {
		float x_center = Window.nowWidth / 2;
		return x / x_center - 1;
	}

	public static float toGLY(float y) {
		float y_center = Window.nowHeight / 2;
		return -(y / y_center - 1);
	}

	public static void showMemoryUsed() {
		long used = ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory();
		double percent = ((double) used) / ChemistryLab.RUNTIME.maxMemory();
		if (percent <= 0.7)
			memory.setFillColor(Color.green);
		else if (percent <= 0.85)
			memory.setFillColor(Color.yellow);
		else
			memory.setFillColor(Color.red);
		memory.setNow(used);
		memory.setMask(ChemistryLab.RUNTIME.totalMemory());
		memory.render(100, 100, Window.nowWidth - 200);
		CommonRender.drawAsciiFont(
				"Memory Used:" + MathHelper.eplison(used / 1048576.0, 1) + "/"
						+ MathHelper.eplison(ChemistryLab.getTotalMemory() / 1048576, 1) + "MB",
				100, 83, 16, Color.black);
	}

	static {
		try {
			asciifont = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
					ResourceManager.getResourceAsStream("assets/textures/font/ascii.png"), GL_NEAREST);
			InitLoader.getTextureLoader().getTextures().put("font.ascii.page", asciifont);
		} catch (Exception e) {
			ChemistryLab.logger.fatal("Can't load ASCII Font", e);
			System.exit(-1);
		}
		memory.setMaskEnabled(true);
	}
}
