package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.hyperic.sigar.*;
import com.chemistrylab.*;
import org.newdawn.slick.*;
import com.chemistrylab.util.*;
import com.chemistrylab.debug.*;
import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.reaction.*;
import com.chemistrylab.eventbus.*;
import com.chemistrylab.layer.effect.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;
import static com.chemistrylab.chemicals.ChemicalsLoader.*;

public class Background extends Layer {

	public static final Sigar sigar = new Sigar();
	public static final Texture table = ChemistryLab.getTextures().get("texture.background.table");

	public Background() {
		super(0, 0, nowWidth, nowHeight);
	}

	private int count = 1;

	@Override
	public void render() {
		// Background Picture
		CommonRender.drawTexture(table, 0, 0, nowWidth, nowHeight, 0, 0, 1, 1);

		// Debug Render Layer
		if (f3) {
			float next = CommonRender.winToOthHeight(CommonRender.formatSize(16));

			// Left part
			CommonRender.drawFont("Chemistry Lab version 1.0_INDEV", 0, 0, 16, Color.white, true);
			CommonRender.drawFont(ChemistryLab.getFPS() + " FPS L:" + LayerRender.getLayerAmount() + " A:"
					+ LayerRender.getAnimationAmount(), 0, next, 16, Color.white, true);
			CommonRender.drawFont("Now Language Environment:" + I18N.getNowLanguage(), 0, next * 2, 16, Color.white,
					true);
			CommonRender.drawFont("Chemicals L:" + chemicals.size() + " F:" + chemicals.getFailedPartLoad() + " A:"
					+ chemicals.atoms() + " I:" + chemicals.ions(), 0, next * 3, 16, Color.white, true);
			CommonRender.drawFont("Reactions L:" + ReactionLoader.reactions.size(), 0, next * 4, 16, Color.white, true);
			CommonRender.drawFont("Tick per second:" + Ticker.getTicks() + "ct/s", 0, next * 5, 16, Color.white, true);

			// Right part
			CommonRender.drawRightFont(
					"Memory Used:" + (CommonRender.RUNTIME.totalMemory() - CommonRender.RUNTIME.freeMemory()) / 1048576
							+ "/" + CommonRender.RUNTIME.maxMemory() / 1048576 + "MB",
					nowWidth, 0, 16, Color.white, true);
			CommonRender.drawRightFont("LWJGL version " + Sys.getVersion(), nowWidth, next, 16, Color.white, true);
			CommonRender.drawRightFont("OpenGL version " + glGetString(GL11.GL_VERSION), nowWidth, next * 2, 16,
					Color.white, true);
			try {
				CpuInfo[] info = sigar.getCpuInfoList();
				CommonRender.drawRightFont("CPU:" + info[0].getVendor() + " " + info[0].getModel(), nowWidth, next * 3,
						16, Color.white, true);
			} catch (SigarException e) {
				CommonRender.drawRightFont("CPU:Cannot get information about CPU", nowWidth, next * 3, 16, Color.red,
						true);
			}
			CommonRender.drawRightFont(
					"Now Resolution: " + Display.getWidth() + " x " + Display.getHeight() + " x "
							+ DISPLAY_MODE.getBitsPerPixel() + " @ " + DISPLAY_MODE.getFrequency() + " Hz",
					nowWidth, next * 4, 16, Color.white, true);
			StringBuilder sb = new StringBuilder("Active Resource Packs: ");
			for(String s : ResourceManager.getResourcePacks()){
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			CommonRender.drawRightFont(sb.toString(), nowWidth, next * 5, 16, Color.white, true);
			CommonRender.drawRightFont("==Environment Infos==", nowWidth, next * 6, 16, Color.white, true);
			CommonRender.drawRightFont("Temperature: " + Environment.getTemperature() + "K", nowWidth, next * 7, 16,
					Color.white, true);
			CommonRender.drawRightFont("Pressure: " + Environment.getPressure() + "Pa", nowWidth, next * 8, 16,
					Color.white, true);
			CommonRender.drawRightFont("Molar Volume of Gas: " + Environment.getGasMolV() + "L/mol", nowWidth, next * 9,
					16, Color.white, true);
			if (!last_ret.isEmpty())
				CommonRender.drawRightFont(last_ret, nowWidth, next * 10, 16, last_failed ? Color.red : Color.yellow,
						true);

			// With SHIFT---A mem & fps version
			if (f3_with_shift) {
				CommonRender.drawFont("FPS Infos:", 0, next * 6, 16, Color.white, true, new Color(255, 10, 10, 100));
				Color.white.bind();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 7 + 155);
					glVertex2f(161, next * 7 + 155);
				glEnd();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 7 + 5);
					glVertex2f(10, next * 7 + 155);
				glEnd();
				Queue<Integer> fpss = DebugSystem.getFPSs();
				new Color(255, 10, 10, 150).bind();
				fpss.forEach(i -> {
					glBegin(GL_QUADS);
						glVertex2f(10 + count, next * 7 + 154 - 150.0f * i / maxFPS * 0.8f);
						glVertex2f(10 + count, next * 7 + 154);
						glVertex2f(10 + count + 1, next * 7 + 154);
						glVertex2f(10 + count + 1, next * 7 + 154 - 150.0f * i / maxFPS * 0.8f);
					glEnd();
					count++;
				});
				count = 1;
				CommonRender.drawFont("Memory Infos:", 0, next * 7 + 160, 16, Color.white, true,
						new Color(255, 10, 10, 100));
				Color.white.bind();
				Queue<Long> mems = DebugSystem.getMems();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 8 + 160 + 155);
					glVertex2f(160, next * 8 + 160 + 155);
				glEnd();
				glBegin(GL_LINE_STRIP);
					glVertex2f(10, next * 8 + 160 + 5);
					glVertex2f(10, next * 8 + 160 + 155);
				glEnd();
				new Color(255, 10, 10, 150).bind();
				glBegin(GL_LINE_STRIP);
				mems.forEach(i -> {
					glVertex2f(10 + count, next * 8 + 160 + 154 - 150.0f * i / CommonRender.RUNTIME.maxMemory());
					count++;
				});
				count = 1;
				glEnd();
			}
		}
	}

	@Override
	public void debugRender() {
		if (useComponent())
			compoRender();
		else
			render();
	}

	private boolean onCommand = false;
	private String last_ret = "";
	private boolean last_failed = false;

	@Override
	public void onKeyActive() {
		super.onKeyActive();
		if (!onCommand && Keyboard.isKeyDown(Keyboard.KEY_C)) {
			TextField f = new TextField(0, nowHeight - 16, nowWidth, nowHeight, this, 16);
			f.addEffect(new BackgroundEffect(new Color(150, 150, 150, 75)));
			f.setEnterEvent(s -> {
				removeAllComponent();
				onCommand = false;
				focus = null;
				if (s.isEmpty())
					return;
				try {
					last_ret = CommandController.runCommand(s);
					last_failed = false;
				} catch (Exception e) {
					last_ret = "Last Command Failed:" + e.getMessage();
					last_failed = true;
				}
			});
			addComponent(f);
			focus = f;
			onCommand = true;
		}
	}

	@Override
	public boolean useComponent() {
		return onCommand;
	}
}
