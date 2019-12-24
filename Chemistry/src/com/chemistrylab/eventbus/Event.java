package com.chemistrylab.eventbus;

public abstract class Event {

	public Event() {}
	
	public static final Event createNewEvent(){
		return new DefaultEvent();
	}
	
	private static class DefaultEvent extends Event{}
}
