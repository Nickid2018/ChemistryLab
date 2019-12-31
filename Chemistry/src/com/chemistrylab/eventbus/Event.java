package com.chemistrylab.eventbus;

import java.util.*;
import com.chemistrylab.init.*;

public abstract class Event implements Cloneable{
	
	private boolean canceled = false;
	protected UUID eventId = MathHelper.getRandomUUID();
	protected Map<Integer,Object> extras = new HashMap<>();

	public Event() {}
	
	public static final Event createNewEvent(){
		return new DefaultEvent();
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		canceled = true;
	}
	
	public void putExtra(int id,Object obj){
		extras.put(id, obj);
	}
	
	public Object getExtra(int id){
		return extras.get(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Event))
			return false;
		return eventId.equals(((Event)obj).eventId);
	}
	
	public abstract Event clone();

	private static class DefaultEvent extends Event{
		
		@Override
		public Event clone() {
			DefaultEvent ret = new DefaultEvent();
			ret.eventId = eventId;
			return ret;
		}
	}
}
