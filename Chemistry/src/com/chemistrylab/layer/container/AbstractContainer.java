package com.chemistrylab.layer.container;

import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.reaction.*;

public abstract class AbstractContainer extends Layer{
	
	protected ChemicalMixture mix;
	protected Texture layer_0;
	protected Texture layer_1;

	public AbstractContainer(int x0, int y0, int x1, int y1) {
		super(x0, y0, x1, y1);
	}

	@Override
	public void render() {
		//Back of container
		CommonRender.drawTexture(layer_0, range.x0, range.y0, range.x1, range.y1, 0, 0, 1, 1);
		//Mixture
		
		//Front of container
		CommonRender.drawTexture(layer_1, range.x0, range.y0, range.x1, range.y1, 0, 0, 1, 1);
	}
	
	@Override
	public void onMouseEvent() {
		
	}
	
	@Override
	public final boolean useComponent() {
		return false;
	}
}
