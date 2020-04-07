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
	private int index = 0;

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

	public TextureRegistry newRegistry(String name, int index) {
		if (locked)
			throw new UnsupportedOperationException();
		TextureRegistry registry = new TextureRegistry(name);
		registry.index = index;
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
		subRegistries.forEach(TextureRegistry::lock);
	}

	public void register(String path) {
		if (locked)
			throw new UnsupportedOperationException();
		texturesPath.add(path);
	}

	public void unregister(String path) {
		if (locked)
			throw new UnsupportedOperationException();
		texturesPath.remove(path);
	}

	public void unregisterAll() {
		if (locked)
			throw new UnsupportedOperationException();
		texturesPath.clear();
	}

	@Override
	public int compareTo(TextureRegistry o) {
		return index - o.index == 0 ? name.compareTo(o.name) : index - o.index;
	}

	public int doInit(AssetManager manager) {
		if (!locked)
			throw new UnsupportedOperationException();
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
