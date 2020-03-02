package com.github.nickid2018.chemistrylab.layer.animation;

import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.render.*;

public class SideBarClose extends Animation {

	private boolean pop;

	public SideBarClose() {
		this(false);
	}

	public SideBarClose(boolean pop) {
		super(10);
		this.pop = pop;
		LayerRender.popLayer(SideBar.class);
	}

	@Override
	public void render(int fp) {
		SideBar.SIDEBAR_QUAD.updateVertex(FastQuad.POSTION_RIGHT_DOWN, SideBar.SIDEBAR_QUAD
				.getVertex(FastQuad.POSTION_RIGHT_DOWN).setXYZ(CommonRender.toGLX((10 - fp) * 20), -1, 0));
		SideBar.SIDEBAR_QUAD.updateVertex(FastQuad.POSTION_RIGHT_UP, SideBar.SIDEBAR_QUAD
				.getVertex(FastQuad.POSTION_RIGHT_UP).setXYZ(CommonRender.toGLX((10 - fp) * 20), 1, 0));
		SideBar.SIDEBAR_QUAD.render();
	}

	@Override
	public void onEnd() {
		if (pop)
			return;
		LayerRender.pushLayer(new ExpandBar());
	}
}