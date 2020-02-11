package com.chemistrylab.layer.animation;

import com.chemistrylab.layer.*;
import com.chemistrylab.render.*;

public class SideBarExpand extends Animation {

	public SideBarExpand() {
		super(10);
	}

	@Override
	public void render(int fp) {
		SideBar.SIDEBAR_QUAD.updateVertex(FastQuad.POSTION_RIGHT_DOWN, SideBar.SIDEBAR_QUAD
				.getVertex(FastQuad.POSTION_RIGHT_DOWN).setXYZ(CommonRender.toGLX((fp + 1) * 20), -1, 0));
		SideBar.SIDEBAR_QUAD.updateVertex(FastQuad.POSTION_RIGHT_UP, SideBar.SIDEBAR_QUAD
				.getVertex(FastQuad.POSTION_RIGHT_UP).setXYZ(CommonRender.toGLX((fp + 1) * 20), 1, 0));
		SideBar.SIDEBAR_QUAD.render();
	}

	@Override
	public void onEnd() {
		LayerRender.pushLayer(new CloseBar());
		LayerRender.pushLayer(new SideBar());
	}
}
