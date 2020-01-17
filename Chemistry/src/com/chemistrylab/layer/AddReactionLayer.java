package com.chemistrylab.layer;

import java.util.*;
import org.lwjgl.input.*;
import org.newdawn.slick.*;
import com.chemistrylab.init.*;
import com.chemistrylab.reaction.*;
import com.chemistrylab.chemicals.*;
import com.chemistrylab.layer.effect.*;
import com.chemistrylab.layer.component.*;

import static org.lwjgl.opengl.GL11.*;
import static com.chemistrylab.ChemistryLab.*;

public class AddReactionLayer extends Layer {

	private boolean isReversible = false;
	private TextComponent rev;
	private PleaseWaitLayer pw;

	public AddReactionLayer() {
		super(0, 0, WIDTH, HEIGHT);
		TextComponent title = new TextComponent(525, 50, 525, 98, this, I18N.getString("addreact.title"), () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(title);
		TextComponent labela = new TextComponent(50, 120, 300, 168, this, I18N.getString("addreact.react"), () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labela);
		TextComponent labelb = new TextComponent(50, 180, 300, 228, this, I18N.getString("addreact.get"), () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labelb);
		TextComponent labelc = new TextComponent(50, 240, 300, 288, this, I18N.getString("addreact.deltaH"), () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labelc);
		TextComponent labeld = new TextComponent(50, 300, 300, 348, this, I18N.getString("addreact.deltaS"), () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labeld);
		TextField reacts = new TextField(300, 128, 1000, 160, this, 32);
		reacts.addEffect(new LineBorderEffect(1, Color.white));
		comps.add(reacts);
		TextField gets = new TextField(300, 188, 1000, 220, this, 32);
		gets.addEffect(new LineBorderEffect(1, Color.white));
		comps.add(gets);
		TextField deltaHs = new TextField(300, 248, 850, 280, this, 32);
		deltaHs.addEffect(new LineBorderEffect(1, Color.white));
		comps.add(deltaHs);
		TextField deltaSs = new TextField(300, 308, 850, 340, this, 32);
		deltaSs.addEffect(new LineBorderEffect(1, Color.white));
		comps.add(deltaSs);
		TextComponent labele = new TextComponent(850, 240, 1000, 288, this, "kJ/mol", () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labele);
		TextComponent labelf = new TextComponent(850, 300, 1000, 348, this, "J/(mol¡¤K)", () -> {
		}, 48, Color.white, true).setAlignCenter();
		comps.add(labelf);
		TextComponent labelg = new TextComponent(300, 360, 400, 408, this, "K=", () -> {
		}, 48, Color.white, true).setAlignCenter();
		TextField ks = new TextField(400, 368, 1000, 400, this, 32);
		ks.addEffect(new LineBorderEffect(1, Color.white));
		rev = new TextComponent(75, 360, 300, 408, this, I18N.getString("addreact.nonreversible"), () -> {
			if (Mouse.isButtonDown(0) && isClickLegal(250)) {
				isReversible = !isReversible;
				rev.setText(isReversible ? I18N.getString("addreact.reversible")
						: I18N.getString("addreact.nonreversible"));
				LayerRender.addEndEvent(() -> {
					if (isReversible) {
						comps.add(labelg);
						comps.add(ks);
					} else {
						comps.remove(labelg);
						comps.remove(ks);
					}
				});
			}
		}, 48, Color.white, true).setAlignCenter();
		rev.addEffect(new LineBorderEffect(3, Color.white));
		comps.add(rev);
		TextComponent ok = new TextComponent(210, 500, 420, 548, this, I18N.getString("program.add"), () -> {
			if (Mouse.isButtonDown(0)) {
				LayerRender.addEndEvent(() -> {
					LayerRender.popLayer(AddReactionLayer.this);
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
							if (isReversible) {
								String strK = ks.getString();
								ReversibleReaction r = new ReversibleReaction(react, get, dH, dS, strK);
								ReactionLoader.reactions.put(r.computeSign(),r);
							} else {
								NonReversibleReaction r = new NonReversibleReaction(react, get, dH, dS);
								ReactionLoader.reactions.put(r.computeSign(),r);
							}
							ReactionLoader.reactions.writeToFile();
						} catch (Exception e) {
							pw.setError(e.getMessage());
						}
					}).start());
				});
			}
		}, 48, Color.white, true).setAlignCenter();
		ok.addEffect(new LineBorderEffect(3, Color.white));
		comps.add(ok);
		TextComponent cancel = new TextComponent(630, 500, 840, 548, this, I18N.getString("program.cancel"), () -> {
			if (Mouse.isButtonDown(0))
				LayerRender.addEndEvent(() -> {
					LayerRender.popLayer(AddReactionLayer.this);
					LayerRender.pushLayer(new ExpandBar());
				});
		}, 48, Color.white, true).setAlignCenter();
		cancel.addEffect(new LineBorderEffect(3, Color.white));
		comps.add(cancel);
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
