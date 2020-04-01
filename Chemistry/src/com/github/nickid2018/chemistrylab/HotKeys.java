package com.github.nickid2018.chemistrylab;

import java.awt.Desktop;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import javax.imageio.*;
import java.awt.image.*;
import org.lwjgl.opengl.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.event.*;

public class HotKeys {

	// Action Keys
	public static boolean f3 = false;
	public static boolean f3_with_shift = false;
	public static boolean f3_with_ctrl = false;
	public static boolean fullScreen = false;

	// HotKey Operations
	public static final void registerMainHotKeys() {
//		HotKeyMap.addHotKey(GLFW.GLFW_KEY_F3, (scancode, action, mods) -> {
//			if (action != GLFW.GLFW_PRESS)
//				return;
			HotKeys.f3 = !HotKeys.f3;
//			EngineChemistryLab.logger.info("Debug Mode:" + (HotKeys.f3 ? "on" : "off"));
//			if (HotKeys.f3) {
//				EventBus.postEvent(DEBUG_ON);
//			} else {
//				EventBus.postEvent(DEBUG_OFF);
//			}
//			HotKeys.f3_with_shift = (mods & GLFW.GLFW_MOD_SHIFT) == GLFW.GLFW_MOD_SHIFT;
//			HotKeys.f3_with_ctrl = (mods & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL;
//		});
//		HotKeyMap.addHotKey(GLFW_KEY_F2, (scancode, action, mods) -> {
//			if (action != GLFW.GLFW_PRESS)
//				return;
			GL11.glReadBuffer(GL11.GL_FRONT);
			int width = 0;
			int height = 0;
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
//					MessageBoard.INSTANCE.addMessage(
//							new Message().addMessageEntry(new MessageEntry(I18N.getString("screenshot.success")))
//									.addMessageEntry(new MessageEntry(file.getAbsolutePath()).setUnderline(true)
//											.setClickEvent((button, action2, mods2) -> {
//												if (button == 0 && isSystemClickLegal(100)
//														&& Desktop.isDesktopSupported()) {
//													try {
//														Desktop.getDesktop().open(file);
//													} catch (Exception e) {
//													}
//												}
//											})));
				} catch (Exception e) {
//					MessageBoard.INSTANCE.addMessage(new Message().addMessageEntry(
//							new MessageEntry(I18N.getString("screenshot.failed")).setColor(Color.RED)));
				}
			});
//		});
//		HotKeyMap.addHotKey(GLFW_KEY_F11, (scancode, action, mods) -> {
//			if (action != GLFW.GLFW_PRESS)
				return;
			// Send Recreate Request
			// P.S. Recreate cannot be run to callback
//			HotKeys.fullScreen = !HotKeys.fullScreen;
//			EngineChemistryLab.logger.info("Fullscreen:" + (HotKeys.fullScreen ? "on" : "off"));
//			MainWindow.recreateWindow = true;
//		});
	}
}
