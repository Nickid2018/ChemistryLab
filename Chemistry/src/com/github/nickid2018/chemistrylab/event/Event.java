package com.github.nickid2018.chemistrylab.event;

import com.github.nickid2018.chemistrylab.util.pool.*;

public abstract class Event implements Poolable {

	@SuppressWarnings("unchecked")
	public static final <T extends Event> T newEvent(Class<T> clz, Object... o) {
		return (T) Pools.obtain(clz).set(o);
	}
	
	public static final void free(Event e) {
		Pools.free(e);
	}

	public abstract Event set(Object... o);
}
