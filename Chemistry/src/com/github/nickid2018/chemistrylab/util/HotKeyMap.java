package com.github.nickid2018.chemistrylab.util;

import java.util.*;
import org.lwjgl.glfw.*;
import com.google.common.eventbus.*;
import com.github.mmc1234.minigoldengine.event.*;

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

	@Subscribe
	public static void keyActive(EventKey key) {
		activeKey(key.getKey(), key.getScancode(), key.getAction(), key.getMods());
	}

	public static void activeKey(int key, int scancode, int action, int mods) {
		keymap.forEach((i, r) -> {
			if (i == key && action == GLFW.GLFW_PRESS)
				r.hotKeyActive(scancode, action, mods);
		});
	}
}
