package com.github.nickid2018.chemistrylab.event;

public abstract class CancellableEvent extends Event {

	private boolean cancelled;

	public void cancel() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
