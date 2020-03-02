package com.github.nickid2018.chemistrylab.render;

import java.util.*;
import org.newdawn.slick.opengl.*;

import com.github.nickid2018.chemistrylab.init.InitLoader;

public class Textures extends HashMap<String, Texture> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5333716862597581852L;

	public void releaseAll() {
		forEach((s, t) -> {
			t.release();
		});
	}

	/**
	 * Get textures
	 * 
	 * @return Textures
	 */
	public static Textures getTextures() {
		return InitLoader.getTextureLoader().getTextures();
	}

}
