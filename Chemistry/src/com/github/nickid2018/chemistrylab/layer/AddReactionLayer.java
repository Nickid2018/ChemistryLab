package com.github.nickid2018.chemistrylab.layer;

import java.util.*;
import org.lwjgl.glfw.*;
import org.newdawn.slick.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.chemicals.*;
import com.github.nickid2018.chemistrylab.layer.effect.*;
import com.github.nickid2018.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;

public class AddReactionLayer extends Layer {

	private boolean isReversible = false;
	private TextComponent rev;
	private PleaseWaitLayer pw;
	private TextField ks;
	private TextComponent kl;

	public AddReactionLayer() {
		super(0, 0, Window.nowWidth, Window.nowHeight);
		// UI
		TextComponent title = new TextComponent(Window.nowWidth / 2, CommonRender.toRatioYPos(50), Window.nowWidth / 2,
				CommonRender.toRatioYPos(98), this, I18N.getString("addreact.title"), (button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(title);
		TextComponent labela = new TextComponent(CommonRender.toRatioXPos(50), CommonRender.toRatioYPos(120),
				CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(168), this, I18N.getString("addreact.react"),
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labela);
		TextComponent labelb = new TextComponent(CommonRender.toRatioXPos(50), CommonRender.toRatioYPos(180),
				CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(228), this, I18N.getString("addreact.get"),
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labelb);
		TextComponent labelc = new TextComponent(CommonRender.toRatioXPos(50), CommonRender.toRatioYPos(240),
				CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(288), this, I18N.getString("addreact.deltaH"),
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labelc);
		TextComponent labeld = new TextComponent(CommonRender.toRatioXPos(50), CommonRender.toRatioYPos(300),
				CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(348), this, I18N.getString("addreact.deltaS"),
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labeld);
		TextField reacts = new TextField(CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(128),
				CommonRender.toRatioXPos(Window.DREAM_WIDTH - 50), CommonRender.toRatioYPos(160), this, 32);
		reacts.addEffect(new LineBorderEffect(1, Color.white));
		addComponent(reacts);
		TextField gets = new TextField(CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(188),
				CommonRender.toRatioXPos(Window.DREAM_WIDTH - 50), CommonRender.toRatioYPos(220), this, 32);
		gets.addEffect(new LineBorderEffect(1, Color.white));
		addComponent(gets);
		TextField deltaHs = new TextField(CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(248),
				CommonRender.toRatioXPos(1000), CommonRender.toRatioYPos(280), this, 32);
		deltaHs.addEffect(new LineBorderEffect(1, Color.white));
		addComponent(deltaHs);
		TextField deltaSs = new TextField(CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(308),
				CommonRender.toRatioXPos(1000), CommonRender.toRatioYPos(340), this, 32);
		deltaSs.addEffect(new LineBorderEffect(1, Color.white));
		addComponent(deltaSs);
		TextComponent labele = new TextComponent(CommonRender.toRatioXPos(1000), CommonRender.toRatioYPos(240),
				CommonRender.toRatioXPos(Window.DREAM_WIDTH - 50), CommonRender.toRatioYPos(288), this, "kJ/mol",
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labele);
		TextComponent labelf = new TextComponent(CommonRender.toRatioXPos(1000), CommonRender.toRatioYPos(300),
				CommonRender.toRatioXPos(Window.DREAM_WIDTH - 50), CommonRender.toRatioYPos(348), this, "J/(mol��K)",
				(button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		addComponent(labelf);
		kl = new TextComponent(CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(360),
				CommonRender.toRatioXPos(400), CommonRender.toRatioYPos(408), this, "K=", (button, action, mods) -> {
				}, 32, Color.white, true).setAlignCenter();
		ks = new TextField(CommonRender.toRatioXPos(400), CommonRender.toRatioYPos(368),
				CommonRender.toRatioXPos(Window.DREAM_WIDTH - 50), CommonRender.toRatioYPos(400), this, 32);
		ks.addEffect(new LineBorderEffect(1, Color.white));
		rev = new TextComponent(CommonRender.toRatioXPos(50), CommonRender.toRatioYPos(360),
				CommonRender.toRatioXPos(300), CommonRender.toRatioYPos(408), this,
				I18N.getString("addreact.nonreversible"), (button, action, mods) -> {
					if (button == 0 && isClickLegal(250) && action == GLFW.GLFW_PRESS) {
						isReversible = !isReversible;
						rev.setText(isReversible ? I18N.getString("addreact.reversible")
								: I18N.getString("addreact.nonreversible"));
						if (isReversible) {
							addComponent(kl);
							addComponent(ks);
						} else {
							removeComponent(kl);
							removeComponent(ks);
						}
					}
				}, 32, Color.white, true).setAlignCenter();
		rev.addEffect(new LineBorderEffect(3, Color.white));
		addComponent(rev);
		TextComponent ok = new TextComponent(CommonRender.toRatioXPos(Window.DREAM_WIDTH / 5),
				CommonRender.toRatioYPos(500), CommonRender.toRatioXPos(Window.DREAM_WIDTH / 5 * 2),
				CommonRender.toRatioYPos(548), this, I18N.getString("program.add"), (button, action, mods) -> {
					if (button == 0 && action == GLFW.GLFW_PRESS) {
						LayerRender.popLayer(this);
						LayerRender.pushLayer(new ExpandBar());
						LayerRender.pushLayer(pw = new PleaseWaitLayer(I18N.getString("program.writing"), () -> {
							// Add reaction
							String[] reactsps = reacts.getString().split(";");
							String[] getsps = gets.getString().split(";");
							Map<ChemicalResource, Integer> react = new HashMap<>();
							Map<ChemicalResource, Integer> get = new HashMap<>();
							try {
								for (String s : reactsps) {
									String[] pair = s.split(":");
									ChemicalResource res = ChemicalsLoader.chemicals.get(pair[0]);
									int count = Integer.parseInt(pair[1]);
									react.put(res, count);
								}
								for (String s : getsps) {
									String[] pair = s.split(":");
									ChemicalResource res = ChemicalsLoader.chemicals.get(pair[0]);
									int count = Integer.parseInt(pair[1]);
									get.put(res, count);
								}
								double dH = Double.parseDouble(deltaHs.getString());
								double dS = Double.parseDouble(deltaSs.getString());
								Reaction r;
								if (isReversible) {
									String strK = ks.getString();
									r = new ReversibleReaction(react, get, dH, dS, strK);
								} else {
									r = new NonReversibleReaction(react, get, dH, dS);
								}
								ReactionLoader.reactions.addNewReaction(r);
								pw.setSuccess(I18N.getString("addreact.success"));
							} catch (Exception e) {
								pw.setError(e.getMessage());
							}
						}).start());
					}
				}, 32, Color.white, true).setAlignCenter();
		ok.addEffect(new LineBorderEffect(3, Color.white));
		addComponent(ok);
		TextComponent cancel = new TextComponent(CommonRender.toRatioXPos(Window.DREAM_WIDTH / 5 * 3),
				CommonRender.toRatioYPos(500), CommonRender.toRatioXPos(Window.DREAM_WIDTH / 5 * 4),
				CommonRender.toRatioYPos(548), this, I18N.getString("program.cancel"), (button, action, mods) -> {
					if (action != GLFW.GLFW_PRESS)
						return;
					if (button == 0) {
						LayerRender.popLayer(AddReactionLayer.this);
						LayerRender.pushLayer(new ExpandBar());
					}
				}, 32, Color.white, true).setAlignCenter();
		cancel.addEffect(new LineBorderEffect(3, Color.white));
		addComponent(cancel);
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
	public void onContainerResized() {
		super.onContainerResized();
		if (!isReversible) {
			doDefaultResize(ks);
			doDefaultResize(kl);
		}
	}

	@Override
	public boolean useComponent() {
		return true;
	}
}