package com.github.nickid2018.chemistrylab.layer.component;

import java.util.*;

import com.github.nickid2018.chemistrylab.layer.*;
import com.github.nickid2018.chemistrylab.layer.effect.*;

public abstract class Component extends Layer {

	protected List<Effect> es;
	protected final Layer parent;

	public Component(float f, float f2, float g, float h, Layer l) {
		this(f, f2, g, h, l, new Effect[0]);
	}

	public Component(float f, float f2, float g, float h, Layer l, Effect... e) {
		super(f, f2, g, h);
		es = new ArrayList<>();
		es.addAll(Arrays.asList(e));
		parent = l;
	}

	@Override
	public void render() {
		for (Effect e : es) {
			e.effect(this);
		}
	}

	public void addEffect(Effect e) {
		es.add(e);
	}

	public void removeEffect(Effect e) {
		es.remove(e);
	}
}
