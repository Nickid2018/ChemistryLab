package com.github.nickid2018.chemistrylab.layer;

import java.util.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import java.awt.Toolkit;
import org.lwjgl.opengl.*;
import org.hyperic.sigar.*;
import org.newdawn.slick.*;
import java.awt.datatransfer.*;

import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.debug.*;
import com.github.nickid2018.chemistrylab.eventbus.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.layer.component.*;
import com.github.nickid2018.chemistrylab.layer.effect.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.sound.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.value.MyHotKeys;
import com.github.nickid2018.chemistrylab.window.FPS;
import com.github.nickid2018.chemistrylab.window.Window;

import org.newdawn.slick.opengl.*;

import static com.github.nickid2018.chemistrylab.ChemistryLab.*;
import static com.github.nickid2018.chemistrylab.chemicals.ChemicalsLoader.*;
import static org.lwjgl.opengl.GL11.*;

public class Background extends Layer {

	public static final Sigar sigar = new Sigar();
	public static final Texture table = Textures.getTextures().get("texture.background.table");
	public static final FastTexture tex = new FastTexture(0, 0, Window.nowWidth, Window.nowHeight, 0, 0, 1, 1, table);

	public Background() {
		super(0, 0, Window.nowWidth, Window.nowHeight);
	}

	private int count = 1;

	@Override
	public void render() {
		// Background Picture
		tex.render();

		// Debug Render Layer
		if (MyHotKeys.f3) {
			float next = CommonRender.winToOthHeight(CommonRender.formatSize(16));

			// Left part
			CommonRender.drawFont("Chemistry Lab version 1.0_INDEV", 0, 0, 16, Color.white, true);
			CommonRender
					.drawFont(
							FPS.getFPS() + " FPS " + FPS.getUPS() + " UPS L:"
									+ LayerRender.getLayerAmount() + " A:" + LayerRender.getAnimationAmount(),
							0, next, 16, Color.white, true);
			CommonRender.drawFont("Now Language Environment:" + I18N.getNowLanguage(), 0, next * 2, 16, Color.white,
					true);
			CommonRender.drawFont("Chemicals L:" + chemicals.size() + " F:" + chemicals.getFailedPartLoad() + " A:"
					+ chemicals.atoms() + " I:" + chemicals.ions(), 0, next * 3, 16, Color.white, true);
			CommonRender.drawFont("Reactions L:" + ReactionLoader.reactions.size(), 0, next * 4, 16, Color.white, true);
			CommonRender.drawFont("Tick per second:" + Ticker.getTicks() + "ct/s", 0, next * 5, 16, Color.white, true);

			// Right part
			CommonRender
					.drawRightFont(
							"Memory Used:"
									+ MathHelper.eplison(
											(ChemistryLab.RUNTIME.totalMemory() - ChemistryLab.RUNTIME.freeMemory())
													/ 1048576.0,
											1)
									+ "/" + MathHelper.eplison(ChemistryLab.getTotalMemory() / 1048576, 1) + "MB",
									Window.nowWidth, 0, 16, Color.white, true);
			CommonRender.drawRightFont("LWJGL version " + Version.getVersion(), Window.nowWidth, next, 16, Color.white, true);
			CommonRender.drawRightFont("GLFW version " + GLFW.glfwGetVersionString(), Window.nowWidth, next * 2, 16,
					Color.white, true);
			CommonRender.drawRightFont("OpenGL version " + glGetString(GL11.GL_VERSION), Window.nowWidth, next * 3, 16,
					Color.white, true);
			CommonRender.drawRightFont(
					"OpenAL version " + SoundSystem.getALVersion() + " ALC " + SoundSystem.getALCVersion(), Window.nowWidth,
					next * 4, 16, Color.white, true);

			try {
				CpuInfo[] info = sigar.getCpuInfoList();
				CommonRender.drawRightFont("CPU:" + info[0].getVendor() + " " + info[0].getModel(), Window.nowWidth, next * 5,
						16, Color.white, true);
			} catch (SigarException e) {
				CommonRender.drawRightFont("CPU:Cannot get information about CPU", Window.nowWidth, next * 5, 16, Color.red,
						true);
			}
			CommonRender.drawRightFont("Now Resolution: " + Window.nowWidth + " x " + Window.nowHeight, Window.nowWidth, next * 6, 16,
					Color.white, true);
			StringBuilder sb = new StringBuilder("Active Resource Packs: ");
			for (String s : ResourceManager.getResourcePacks()) {
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			CommonRender.drawRightFont(sb.toString(), Window.nowWidth, next * 7, 16, Color.white, true);
			CommonRender.drawRightFont("==Environment Infos==", Window.nowWidth, next * 8, 16, Color.white, true);
			CommonRender.drawRightFont("Temperature: " + Environment.getTemperature() + "K", Window.nowWidth, next * 9, 16,
					Color.white, true);
			CommonRender.drawRightFont("Pressure: " + Environment.getPressure() + "Pa", Window.nowWidth, next * 10, 16,
					Color.white, true);
			CommonRender.drawRightFont("Molar Volume of Gas: " + Environment.getGasMolV() + "L/mol", Window.nowWidth,
					next * 11, 16, Color.white, true);

			// With SHIFT---A mem & fps version
			if (MyHotKeys.f3_with_shift) {
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
					glVertex2f(10 + count, next * 7 + 154 - 150.0f * i / FPS.maxFPS * 0.8f);
					glVertex2f(10 + count, next * 7 + 154);
					glVertex2f(10 + count + 1, next * 7 + 154);
					glVertex2f(10 + count + 1, next * 7 + 154 - 150.0f * i / FPS.maxFPS * 0.8f);
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
					glVertex2f(10 + count, next * 8 + 160 + 154 - 150.0f * i / ChemistryLab.RUNTIME.maxMemory());
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
	private TextField f;

	@Override
	public void onKeyActive(int key, int scancode, int action, int mods) {
		super.onKeyActive(key, scancode, action, mods);
		if (onCommand)
			focus = f;
		if (action != GLFW.GLFW_PRESS)
			return;
		if (!onCommand && key == GLFW.GLFW_KEY_SLASH) {
			f = new TextField(0, Window.nowHeight - 16, Window.nowWidth, Window.nowHeight, this, 16);
			f.addEffect(new BackgroundEffect(new Color(150, 150, 150, 75), f));
			f.setEnterEvent(s -> {
				removeAllComponent();
				onCommand = false;
				focus = null;
				f = null;
				if (s.isEmpty())
					return;
				try {
					Message[] m = CommandController.runCommand(s);
					for (Message mess : m) {
						MessageBoard.INSTANCE.addMessage(mess);
					}
				} catch (Exception e) {
					Message m = new Message().addMessageEntry(
							new MessageEntry(String.format(I18N.getString("command.failed"), e.getMessage()))
									.setColor(Color.red).setClickEvent((button, action2, mods2) -> {
										if (button != 0)
											return;
										Transferable trans = new StringSelection(s);
										Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
									}));
					MessageBoard.INSTANCE.addMessage(m);
				}
			});
			addComponent(f);
			focus = f;
			onCommand = true;
		}
	}

	@Override
	public void gainFocus() {
		if (f != null) {
			focus = f;
		}
	}

	@Override
	public boolean useComponent() {
		return onCommand;
	}
}
