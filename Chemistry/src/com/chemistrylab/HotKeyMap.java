package com.chemistrylab;

import java.util.*;
import org.lwjgl.glfw.*;

//Called in Key Callback
public class HotKeyMap {

	@FunctionalInterface
	public static interface HotKeyReference {

		void hotKeyActive(int scancode, int action, int mods);
	}

	private static final Map<Integer, HotKeyReference> keymap = new HashMap<>();

	public static void addHotKey(int key, HotKeyReference ref) {
		keymap.put(key, ref);
	}

	public static void removeHotKey(int key) {
		keymap.remove(key);
	}

	public static void replaceHotKey(int key, HotKeyReference ref) {
		keymap.replace(key, ref);
	}

	public static void activeKey(int key, int scancode, int action, int mods) {
		keymap.forEach((i, r) -> {
			if (i == key && action == GLFW.GLFW_PRESS)
				r.hotKeyActive(scancode, action, mods);
		});
	}
}
