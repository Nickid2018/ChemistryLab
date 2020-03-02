package com.github.nickid2018.chemistrylab.render;

import java.util.*;
import org.newdawn.slick.opengl.*;

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

}
