package com.github.nickid2018.chemistrylab.value;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import com.github.nickid2018.chemistrylab.ChemistryLab;
import com.github.nickid2018.chemistrylab.HotKeyMap;
import com.github.nickid2018.chemistrylab.eventbus.EventBus;
import com.github.nickid2018.chemistrylab.eventbus.ThreadManger;
import com.github.nickid2018.chemistrylab.layer.MessageBoard;
import com.github.nickid2018.chemistrylab.util.I18N;
import com.github.nickid2018.chemistrylab.util.Message;
import com.github.nickid2018.chemistrylab.util.MessageEntry;
import com.github.nickid2018.chemistrylab.window.Window;

public class MyHotKeys {

	// Action Keys
	public static boolean f3 = false;
	public static boolean f3_with_shift = false;
	public static boolean f3_with_ctrl = false;
	public static boolean fullScreen = false;

	public static void init() {
		HotKeyMap.addHotKey(GLFW.GLFW_KEY_F3, (scancode, action, mods) -> {
			if (action != GLFW.GLFW_PRESS)
				return;
			MyHotKeys.f3 = !MyHotKeys.f3;
			ChemistryLab.logger.info("Debug Mode:" + (MyHotKeys.f3 ? "on" : "off"));
			if (MyHotKeys.f3) {
				EventBus.postEvent(ChemistryLab.DEBUG_ON);
			} else {
				EventBus.postEvent(ChemistryLab.DEBUG_OFF);
			}
			MyHotKeys.f3_with_shift = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
			MyHotKeys.f3_with_ctrl = (mods & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL;
		});
		HotKeyMap.addHotKey(GLFW_KEY_F2, (scancode, action, mods) -> {
			if (action != GLFW.GLFW_PRESS)
				return;
			GL11.glReadBuffer(GL11.GL_FRONT);
			int width = (int) Window.nowWidth;
			int height = (int) Window.nowHeight;
			int bpp = 4; // Assuming a 32-bit display with a byte each for
							// red, green, blue, and alpha.
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			// Run in Thread Manager
			// Concurrent Operation
			ThreadManger.invoke(() -> {
				Date date = new Date();
				File file = new File("screenshot/screenshot_"
						+ String.format("%tY%tm%td%tH%tM%tS%tL", date, date, date, date, date, date, date) + ".png"); // The
																														// file
																														// to
																														// save
																														// to.
				String format = "PNG";
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int i = (x + (width * y)) * bpp;
						int r = buffer.get(i) & 0xFF;
						int g = buffer.get(i + 1) & 0xFF;
						int b = buffer.get(i + 2) & 0xFF;
						image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
					}
				}
				try {
					ImageIO.write(image, format, file);
					// Yes, as you see, you are blind!
					MessageBoard.INSTANCE.addMessage(
							new Message().addMessageEntry(new MessageEntry(I18N.getString("screenshot.success")))
									.addMessageEntry(new MessageEntry(file.getAbsolutePath()).setUnderline(true)
											.setClickEvent((button, action2, mods2) -> {
												if (button == 0 && ChemistryLab.isSystemClickLegal(100)
														&& Desktop.isDesktopSupported()) {
													try {
														Desktop.getDesktop().open(file);
													} catch (Exception e) {
													}
												}
											})));
				} catch (Exception e) {
					MessageBoard.INSTANCE.addMessage(new Message().addMessageEntry(
							new MessageEntry(I18N.getString("screenshot.failed")).setColor(Color.red)));
				}
			});
		});
		HotKeyMap.addHotKey(GLFW_KEY_F11, (scancode, action, mods) -> {
			if (action != GLFW.GLFW_PRESS)
				return;
			// Send Recreate Request
			// P.S. Recreate cannot be run to callback
			MyHotKeys.fullScreen = !MyHotKeys.fullScreen;
			ChemistryLab.logger.info("Fullscreen:" + (MyHotKeys.fullScreen ? "on" : "off"));
			Window.recreateWindow = true;
		});
	}

}
