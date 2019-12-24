package com.chemistrylab.eventbus;

public interface EventBusListener {

	public default boolean receiveEvents(Event e){
		return true;
	}
	
	public void listen(Event e);
}
