package com.github.nickid2018.chemistrylab.layer.container;

import java.util.*;
import org.lwjgl.glfw.*;
import com.alibaba.fastjson.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.render.*;
import com.github.nickid2018.chemistrylab.window.*;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.eventbus.*;

public abstract class AbstractContainer extends Layer {

	protected ChemicalMixture mix;
	protected RangeTexture layer_0;
	protected RangeTexture layer_1;
	protected final Size size;
	protected UUID uuid = MathHelper.getRandomUUID();
	protected boolean broken = false;

	public AbstractContainer(int x0, int y0, Size s) {
		super(x0, y0, x0 + s.diameter, y0 + s.height);
		size = s;
		mix = new ChemicalMixture();
	}

	@Override
	public void render() {
		// Back of container
//		layer_0.drawTexture(range.x0, range.y0, range.x1, range.y1);
		// Mixture

		// Front of container
//		layer_1.drawTexture(range.x0, range.y0, range.x1, range.y1);
	}

	public UUID getUUID() {
		return uuid;
	}

	public Size getSize() {
		return size;
	}

	public boolean isBroken() {
		return broken;
	}

	public void breakContainer() {
		broken = true;
	}

	public void remove() {
		EventBus.removeListener(mix.getController());
	}

	public JSONObject specials(String json) {
		JSONObject obj = JSON.parseObject(json);
		if (obj.containsKey("broken")) {
			broken = obj.getBooleanValue("broken");
		}
		if (obj.containsKey("uuid")) {
			uuid = UUID.fromString(obj.getString("uuid"));
		}
		return obj;
	}

	public void addChemical(Unit u) {
		mix.put(u.getChemical(), u);
	}

	@Override
	public void onMouseEvent(int button, int action, int mods) {
		if (action != GLFW.GLFW_PRESS)
			return;
		if (button != 0) {
			float dx = (float) Mouse.getDX();
			float dy = (float) Mouse.getDY();
			range.x0 += dx;
			range.x1 += dx;
			range.y0 += dy;
			range.y1 += dy;
		}
	}

	@Override
	public final boolean useComponent() {
		return false;
	}
}
