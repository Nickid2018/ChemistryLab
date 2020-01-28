package com.chemistrylab.eventbus;

import java.util.*;
import com.chemistrylab.init.*;

public abstract class Event implements Cloneable ,Comparable<Event>{

	protected final String name;
	protected boolean canceled = false;
	protected UUID eventId = MathHelper.getRandomUUID();
	protected Map<Integer, Object> extras = new HashMap<>();

	public Event(String name) {
		this.name = name;
	}

	/**
	 * Create a new event and register it.
	 * 
	 * @param name
	 *            The name of the event
	 * @return A new event
	 */
	public static final Event createNewEvent(String name) {
		DefaultEvent e = new DefaultEvent(name);
		EventBus.registerEvent(e);
		return e;
	}

	/**
	 * Return true if the event has been canceled.
	 * 
	 * @return True if the event has been canceled
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * Cancel the event.
	 */
	public void cancel() {
		canceled = true;
	}

	/**
	 * Put an extra information into the event.
	 * 
	 * @param id
	 *            The ID of the object
	 * @param obj
	 *            An object to put
	 */
	public void putExtra(int id, Object obj) {
		extras.put(id, obj);
	}

	/**
	 * Get the extra information from the event.
	 * 
	 * @param id
	 *            The ID of the object
	 * @return An object of the ID.
	 */
	public Object getExtra(int id) {
		return extras.get(id);
	}

	/**
	 * Get the name of the event.
	 * 
	 * @return The name of the event
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the UUID of the event.
	 * 
	 * @return The UUID of the event
	 */
	public UUID getEventId() {
		return eventId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Event))
			return false;
		return eventId.equals(((Event) obj).eventId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getName());
		if (!extras.isEmpty()) {
			sb.append("[");
			for (Map.Entry<Integer, Object> en : extras.entrySet()) {
				sb.append(en.getKey() + ":" + en.getValue() + ",");
			}
			sb.replace(sb.length() - 1, sb.length(), "]");
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(Event o) {
		return name.compareTo(o.name);
	}

	/**
	 * Clone the event into a new copy with the same UUID.
	 * 
	 * @return A new copy with the same UUID
	 */
	public abstract Event clone();

	private static class DefaultEvent extends Event {

		public DefaultEvent(String name) {
			super(name);
		}

		@Override
		public Event clone() {
			DefaultEvent ret = new DefaultEvent(name);
			ret.eventId = eventId;
			return ret;
		}
	}
}
