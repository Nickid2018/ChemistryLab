package com.github.nickid2018.chemistrylab.init;

import java.util.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.github.nickid2018.chemistrylab.mod.*;

public class TextureRegistry implements Comparable<TextureRegistry> {

	private Set<TextureRegistry> subRegistries = new TreeSet<>();
	protected Set<String> texturesPath = new TreeSet<>();
	private boolean locked = false;
	private final String name;

	protected TextureRegistry(String name) {
		this.name = name;
	}

	public TextureRegistry newRegistry(String name) {
		if (locked)
			throw new UnsupportedOperationException();
		TextureRegistry registry = new TextureRegistry(name);
		subRegistries.add(registry);
		return registry;
	}

	public ModTextureRegistry newModRegistry(String modid) {
		if (locked)
			throw new UnsupportedOperationException();
		ModTextureRegistry registry = new ModTextureRegistry(modid);
		subRegistries.add(registry);
		return registry;
	}

	public String getName() {
		return name;
	}

	public void lock() {
		locked = true;
	}

	public void register(String path) {
		texturesPath.add(path);
	}

	public void unregister(String path) {
		texturesPath.remove(path);
	}

	public void unregisterAll() {
		texturesPath.clear();
	}

	@Override
	public int compareTo(TextureRegistry o) {
		return name.compareTo(o.name);
	}

	public int doInit(AssetManager manager) {
		texturesPath.forEach(path -> manager.load(path, Texture.class));
		subRegistries.forEach(registry -> registry.doInit(manager));
		return texturesPath.size();
	}

	public int getTotalSize() {
		int size = texturesPath.size();
		for (TextureRegistry registry : subRegistries) {
			size += registry.getTotalSize();
		}
		return size;
	}

	public ProgressInfo getProgress(int number) {
		ProgressInfo info = new ProgressInfo();
		if (number < texturesPath.size()) {
			info.name = name;
			info.progress = number;
			info.all = texturesPath.size();
		} else {
			int counter = texturesPath.size();
			for (TextureRegistry registry : subRegistries) {
				counter += registry.getTotalSize();
				if (number < counter) {
					return registry.getProgress(number - counter + registry.getTotalSize());
				}
			}
		}
		return info;
	}

	public class ProgressInfo {
		public String name;
		public int progress;
		public int all;
	}
}
