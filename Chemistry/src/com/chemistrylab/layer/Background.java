package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.hyperic.sigar.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;
import static com.chemistrylab.chemicals.ChemicalsLoader.*;

public class Background extends Layer {

	public static final Sigar sigar = new Sigar();
	public static final Texture table = ChemistryLab.getTextures().get("texture.background.table");

	public Background() {
		super(0, 0, 0, 0);
	}

	private int count = 1;

	@Override
	public void render() {
		// Background Picture
		CommonRender.drawTexture(table, 0, 0, WIDTH, HEIGHT, 0, 0, 1, 1);

		// Debug Render Layer
		if (f3) {
			float next = CommonRender.winToOthHeight(16);

			// Left part
			CommonRender.drawFont("Chemistry Lab version 1.0_INDEV", 0, 0, 16, Color.white, true);
			CommonRender.drawFont(ChemistryLab.getFPS() + " FPS L:" + LayerRender.getLayerAmount() + " A:"
					+ LayerRender.getAnimationAmount(), 0, next, 16, Color.white, true);
			CommonRender.drawFont("Now Language Environment:" + I18N.getNowLanguage(), 0, next * 2, 16, Color.white,
					true);
			CommonRender.drawFont("Chemicals L:" + chemicals.size() + " F:" + chemicals.getFailedPartLoad() + " A:"
					+ chemicals.atoms() + " I:" + chemicals.ions(), 0, next * 3, 16, Color.white, true);
			CommonRender.drawFont("Tick per second:" + Ticker.getTicks() + "ct/s", 0, next * 4, 16, Color.white, true);

			// Right part
			CommonRender.drawRightFont(
					"Memory Used:" + (CommonRender.RUNTIME.totalMemory() - CommonRender.RUNTIME.freeMemory()) / 1048576
							+ "/" + CommonRender.RUNTIME.maxMemory() / 1048576 + "MB",
					WIDTH, 0, 16, Color.white, true);
			CommonRender.drawRightFont("LWJGL version " + Sys.getVersion(), WIDTH, next, 16, Color.white, true);
			CommonRender.drawRightFont("OpenGL version " + glGetString(GL11.GL_VERSION), WIDTH, next * 2, 16,
					Color.white, true);
			try {
				CpuInfo[] info = sigar.getCpuInfoList();
				CommonRender.drawRightFont("CPU:" + info[0].getVendor() + " " + info[0].getModel(), WIDTH, next * 3, 16,
						Color.white, true);
			} catch (SigarException e) {
				CommonRender.drawRightFont("CPU:Cannot get information about CPU", WIDTH, next * 3, 16, Color.white,
						true);
			}

			// With SHIFT---A mem & fps version
			if (f3_with_shift) {
				CommonRender.drawFont("FPS Infos:", 0, next * 5, 16, Color.white, true, new Color(255, 10, 10, 100));
				Color.white.bind();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 6 + 155);
					glVertex2f(161, next * 6 + 155);
				glEnd();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 6 + 5);
					glVertex2f(10, next * 6 + 155);
				glEnd();
				Queue<Integer> fpss = DebugSystem.getFPSs();
				new Color(255, 10, 10, 150).bind();
				fpss.forEach(i -> {
					glBegin(GL_QUADS);
						glVertex2f(10 + count, next * 6 + 154 - 150.0f * i / maxFPS * 0.8f);
						glVertex2f(10 + count, next * 6 + 154);
						glVertex2f(10 + count + 1, next * 6 + 154);
						glVertex2f(10 + count + 1, next * 6 + 154 - 150.0f * i / maxFPS * 0.8f);
					glEnd();
					count++;
				});
				count = 1;
				CommonRender.drawFont("Memory Infos:", 0, next * 6 + 160, 16, Color.white, true,
						new Color(255, 10, 10, 100));
				Color.white.bind();
				Queue<Long> mems = DebugSystem.getMems();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 7 + 160 +155);
					glVertex2f(160, next * 7 + 160 + 155);
				glEnd();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 7 + 160 + 5);
					glVertex2f(10, next * 7 + 160 + 155);
				glEnd();
				new Color(255, 10, 10, 150).bind();
				glBegin(GL_LINE_STRIP);
				mems.forEach(i -> {
					glVertex2f(10 + count, next * 7 + 160 + 154 - 150.0f * i / CommonRender.RUNTIME.maxMemory());
					count++;
				});
				count = 1;
				glEnd();
			}
		}
	}
}
