package com.github.nickid2018.chemistrylab.init;

import org.lwjgl.opengl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.github.mmc1234.pinkengine.*;
import com.github.nickid2018.chemistrylab.resource.*;

public class ReactionTableWorld extends World {

	public ReactionTableWorld(PinkEngine engine) {
		super(engine);
		Display.setResizable(true);
		Box box = createBox();
		setBox(box);
		TextureRegion tex_background = new TextureRegion(
				engine.manager.<Texture>get(NameMapping.mapName("gui.table.texture")));
		tex_background.flip(false, true);
		Image2D background = new Image2D(tex_background, camera2D);
		background.isPixel = false;
		background.setLayer(0);
		background.setPosition(0, 0);
		background.setSize(1, 1);
		box.add(background);
	}

}
