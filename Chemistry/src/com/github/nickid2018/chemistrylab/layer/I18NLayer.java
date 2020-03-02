package com.github.nickid2018.chemistrylab.layer;

import java.util.*;
import org.lwjgl.glfw.*;
import org.newdawn.slick.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.layer.effect.*;
import com.github.nickid2018.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;

public class I18NLayer extends Layer {

	private VerticalSlideBar langs;

	public I18NLayer() {
		super(0, 0, Window.nowWidth, Window.nowHeight);
		langs = new VerticalSlideBar(CommonRender.toRatioXPos(Window.nowWidth / 2 - 100), CommonRender.toRatioYPos(48),
				CommonRender.toRatioXPos(Window.nowWidth / 2 + 300), CommonRender.toRatioYPos(Window.nowHeight - 100),
				this, null, CommonRender.getFontHeightUNI("English", 32), 20);
		langs.addEffect(new LineBorderEffect(3, Color.white));
		flush();
		addComponent(langs);
		addComponent(new TextComponent(CommonRender.toRatioXPos(100), CommonRender.toRatioYPos(48),
				CommonRender.toRatioXPos(250), CommonRender.toRatioYPos(96), this, I18N.getString("i18n.change.title"),
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter());
		TextComponent cancel = new TextComponent(CommonRender.toRatioXPos(100), CommonRender.toRatioYPos(120),
				CommonRender.toRatioXPos(250), CommonRender.toRatioYPos(168), this, I18N.getString("program.cancel"),
				(button, action, mods) -> {
					if (button == 0 && action == GLFW.GLFW_PRESS) {
						LayerRender.popLayer(I18NLayer.this);
						LayerRender.pushLayer(new ExpandBar());
					}
				}, 32, Color.white, true).setAlignCenter();
		cancel.addEffect(new LineBorderEffect(3, Color.white));
		addComponent(cancel);
	}

	private void flush() {
		ArrayList<Slidable> s = new ArrayList<>();
		for (String lang : I18N.getSurporttedLanguages()) {
			SlideTextComponent c = new SlideTextComponent(I18NLayer.this, lang, (button, action, mods) -> {
				if (button == 0 && !I18N.getSurporttedLanguageName(lang).equalsIgnoreCase(I18N.getNowLanguage())) {
					LayerRender.popLayer(this);
					LayerRender.pushLayer(new PleaseWaitLayer(I18N.getString("i18n.change.lang"), () -> {
						try {
							I18N.reload(lang);
						} catch (Exception e) {
							I18N.logger.error("Can't reload language!", e);
						}
					}).start());
					LayerRender.pushLayer(new ExpandBar());
				}
			}, 32, Color.white, true);
			if (I18N.getSurporttedLanguageName(lang).equalsIgnoreCase(I18N.getNowLanguage()))
				c.addEffect(new LineBorderEffect(1, Color.yellow));
			s.add(c);
		}
		langs.setComponent(s);
	}

	@Override
	public void render() {
		new Color(150, 150, 150, 75).bind();
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(0, Window.nowHeight);
		glVertex2f(Window.nowWidth, Window.nowHeight);
		glVertex2f(Window.nowWidth, 0);
		glEnd();
	}

	@Override
	public boolean useComponent() {
		return true;
	}
}
