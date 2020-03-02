package com.github.nickid2018.chemistrylab.eventbus;

import java.util.*;

import com.github.nickid2018.chemistrylab.init.*;

public abstract class Event implements Cloneable, Comparable<Event> {

	public static final Event NULL_EVENT = Event.createNewEvent("Null");

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
	 * @param name The name of the event
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
	 * @param id  The ID of the object
	 * @param obj An object to put
	 */
	public void putExtra(int id, Object obj) {
		extras.put(id, obj);
	}

	/**
	 * Get the extra information from the event.
	 * 
	 * @param id The ID of the object
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

	public boolean strictEquals(Object obj) {
		return super.equals(obj);
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
	@Override
	public abstract Event clone();

	public static class CompleteComparedEvent extends Event {

		private Event ev;

		public CompleteComparedEvent(Event e) {
			super(e.name);
			ev = e;
			canceled = e.canceled;
			eventId = e.eventId;
			extras = e.extras;
		}

		@Override
		public int compareTo(Event o) {
			return super.compareTo(o) + extras.hashCode() - o.extras.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return ev.strictEquals(obj);
		}

		@Override
		public Event clone() {
			return null;
		}

		@Override
		public String toString() {
			return ev.toString();
		}
	}

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
