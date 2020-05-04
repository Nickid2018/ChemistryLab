package com.github.nickid2018.chemistrylab.event;

public class FatalErrorEvent extends Event {

	public Thread errorthread;
	public Throwable error;

	@Override
	public void reset() {
		errorthread = null;
		error = null;
	}

	@Override
	public Event set(Object... o) {
		errorthread = (Thread) o[0];
		error = (Throwable) o[1];
		return this;
	}
}
