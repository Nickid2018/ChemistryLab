package com.github.nickid2018.chemistrylab.init;

import com.badlogic.gdx.graphics.*;
import com.github.mmc1234.pinkengine.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class UIStyles {

	public static ProgressBar.ProgressBarStyle PROGRESS_BAR_STYLE;
	public static TextField.TextFieldStyle TEXTFIELD_STYLE;

	public static void initStyles(PinkEngine engine) {
		// TextField
		engine.manager.load("assets/textures/gui/textfield.png", Texture.class);
		engine.manager.finishLoading();
		TEXTFIELD_STYLE = new TextField.TextFieldStyle();
		Texture textfield = engine.manager.get("assets/textures/gui/textfield.png");
		
	}
}
