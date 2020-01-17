package com.chemistrylab.layer.component;

import java.util.*;
import com.chemistrylab.layer.*;
import com.chemistrylab.layer.effect.*;

public abstract class Component extends Layer {
	
	protected List<Effect> es;
	protected final Layer parent;

	public Component(int x0, int y0, int x1, int y1,Layer l) {
		this(x0, y0, x1, y1,l,new Effect[0]);
	}

	public Component(int x0, int y0, int x1, int y1,Layer l,Effect...e) {
		super(x0, y0, x1, y1);
		es=new ArrayList<>();
		es.addAll(Arrays.asList(e));
		parent = l;
	}
	
	@Override
	public void render() {
		for(Effect e:es){
			e.effect(this);
		}
	}
	
	public void addEffect(Effect e){
		es.add(e);
	}
	
	public void removeEffect(Effect e){
		es.remove(e);
	}
}
