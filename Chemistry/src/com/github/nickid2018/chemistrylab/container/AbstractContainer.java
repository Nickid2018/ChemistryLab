package com.github.nickid2018.chemistrylab.container;

import java.util.*;
import com.alibaba.fastjson.*;
import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.nickid2018.chemistrylab.reaction.*;

public abstract class AbstractContainer {

	public static final EventBus CHEMICAL_BUS = new EventBus("ChemicalBus");

	protected ChemicalMixture mix;
//	protected RangeTexture layer_0;
//	protected RangeTexture layer_1;
	protected final Size size;
	protected UUID uuid = MathHelper.getRandomUUID();
	protected boolean broken = false;

	public AbstractContainer(int x0, int y0, Size s) {
		size = s;
		mix = new ChemicalMixture();
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
		CHEMICAL_BUS.unregister(mix.getController());
		Ticker.TICK_EVENT_BUS.unregister(mix.getController());
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
		mix.put(u.getChemical().getDefaultItem(), u);
	}
}
