package com.chemistrylab.eventbus;

/**
 * Listener to receive EventBus events.
 * 
 * @author Nickid2018
 * @see EventBus
 */
public interface EventBusListener {

	/**
	 * Return true if the listener will receive the event.
	 * 
	 * @param e The event to check
	 * @return True if the listener will receive the event
	 */
	public default boolean receiveEvents(Event e) {
		return true;
	}

	/**
	 * Receive an event.
	 * 
	 * @param e The event to receive
	 */
	public void listen(Event e);
}
