package com.chemistrylab.layer.container;

import com.chemistrylab.init.*;
import org.newdawn.slick.opengl.*;

public class ContainerReagent extends AbstractContainer {

	public ContainerReagent(int x0, int y0, Size s) {
		super(x0, y0, s);
		Texture[] layers = InitLoader.getContainerLoader().getLayers();
		layer_0 = layers[0];
		layer_1 = layers[1];
	}

}
