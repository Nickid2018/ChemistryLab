package com.chemistrylab;

import java.util.*;

import org.lwjgl.input.Keyboard;

public class HotKeyMap {

	@FunctionalInterface
	public static interface HotKeyReference{
		void hotKeyActive();
	}
	
	private static final Map<Integer,HotKeyReference> keymap=new HashMap<>();
	
	public static void addHotKey(int key,HotKeyReference ref){
		keymap.put(key, ref);
	}
	
	public static void removeHotKey(int key){
		keymap.remove(key);
	}
	
	public static void replaceHotKey(int key,HotKeyReference ref){
		keymap.replace(key, ref);
	}
	
	public static void activeKey(){
		keymap.forEach((i,r)->{
			if(Keyboard.isKeyDown(i))
				r.hotKeyActive();
		});
	}
}
