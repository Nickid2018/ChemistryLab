package com.github.nickid2018.chemistrylab.event;

import com.github.nickid2018.chemistrylab.properties.*;

public class EnvironmentEvent extends Event {

	public String changedItem;
	public Property<?> oldValue;

	@Override
	public void reset() {
		changedItem = null;
		oldValue = null;
	}

	@Override
	public Event set(Object... o) {
		changedItem = (String) o[0];
		oldValue = (Property<?>) o[1];
		return this;
	}
}
