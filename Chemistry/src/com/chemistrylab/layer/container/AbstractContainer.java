package com.chemistrylab.layer.container;

import java.util.*;
import com.alibaba.fastjson.*;
import com.chemistrylab.init.*;
import com.chemistrylab.layer.*;
//import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.reaction.*;

public abstract class AbstractContainer extends Layer {

	protected ChemicalMixture mix;
	protected Texture layer_0;
	protected Texture layer_1;
	protected final Size size;
	protected final UUID uuid = MathHelper.getRandomUUID();
	protected boolean broken = false;

	public AbstractContainer(int x0, int y0, Size s) {
		super(x0, y0, x0 + s.diameter, y0 + s.height);
		size = s;
	}

	@Override
	public void render() {
		// Back of container
//		CommonRender.drawTexture(layer_0, range.x0, range.y0, range.x1, range.y1, 0, 0, 1, 1);
		// Mixture

		// Front of container
//		CommonRender.drawTexture(layer_1, range.x0, range.y0, range.x1, range.y1, 0, 0, 1, 1);
	}
	
	public UUID getUUID(){
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

	public JSONObject specials(String json){
		JSONObject obj = JSON.parseObject(json);
		if(obj.containsKey("broken")){
			broken = obj.getBooleanValue("broken");
		}
		return obj;
	}

	@Override
	public void onMouseEvent() {

	}

	@Override
	public final boolean useComponent() {
		return false;
	}
}
