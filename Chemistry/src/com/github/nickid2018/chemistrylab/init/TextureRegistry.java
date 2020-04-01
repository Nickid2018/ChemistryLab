package com.github.nickid2018.chemistrylab.init;

import java.util.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.github.nickid2018.chemistrylab.mod.*;

public class TextureRegistry {

	private Set<TextureRegistry> subRegistries = new HashSet<>();
	protected Set<String> texturesPath = new HashSet<>();
	private boolean locked = false;

	protected TextureRegistry() {
	}

	public TextureRegistry newRegistry() {
		if (locked)
			throw new UnsupportedOperationException();
		TextureRegistry registry = new TextureRegistry();
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

	public int doInit(AssetManager manager) {
		texturesPath.forEach(path -> manager.load(path, Texture.class));
		subRegistries.forEach(registry -> registry.doInit(manager));
		return texturesPath.size();
	}
}
