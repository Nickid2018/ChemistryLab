package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.layer.effect.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class I18NLayer extends Layer {
	
	private VerticalSlideBar langs;

	public I18NLayer() {
		super(0, 0, WIDTH, HEIGHT);
		langs=new VerticalSlideBar(WIDTH / 2 - 100, 48, WIDTH / 2 + 300, HEIGHT-100, this, null, 32, 20);
		langs.addEffect(new LineBorderEffect(3, Color.white));
		flush();
		comps.add(langs);
		comps.add(new TextComponent(100, 48, 100, 96, this, I18N.getString("i18n.change.title"), ()->{}, 48, Color.white,true));
		TextComponent cancel=new TextComponent(100, 120, 250, 168, this, I18N.getString("program.cancel"), ()->{
			if (Mouse.isButtonDown(0))
				LayerRender.addEndEvent(() -> {
					LayerRender.popLayer(I18NLayer.this);
				});
		}, 48, Color.white,true);
		cancel.addEffect(new LineBorderEffect(3, Color.white));
		comps.add(cancel);
	}
	
	private void flush(){
		ArrayList<Slidable> s = new ArrayList<>();
		for (String lang : I18N.getSurporttedLanguages()) {
			SlideTextComponent c = new SlideTextComponent(I18NLayer.this, lang, () -> {
				if (Mouse.isButtonDown(0)
						&& !I18N.getSurporttedLanguageName(lang).equalsIgnoreCase(I18N.getNowLanguage())){
					LayerRender.popLayer(this);
					LayerRender.addEndEvent(() -> {
						LayerRender.pushLayer(new PleaseWaitLayer(I18N.getString("i18n.change.lang"), () -> {
							try {
								I18N.reload(lang);
							} catch (Exception e) {
								I18N.logger.error("Can't reload language!", e);
							}
						}).start());
					});
				}
			}, 32, Color.white);
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
			glVertex2f(0, HEIGHT);
			glVertex2f(WIDTH, HEIGHT);
			glVertex2f(WIDTH, 0);
		glEnd();
	}

	@Override
	public boolean useComponent() {
		return true;
	}
}
