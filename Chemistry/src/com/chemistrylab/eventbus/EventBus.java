package com.chemistrylab.eventbus;

import java.util.*;
import java.util.concurrent.*;

public class EventBus {

	public static final Event AWAIT_EVENT_RUN_OVER = Event.createNewEvent();
	public static final Event AWAIT_EVENT_TIMEOUT = Event.createNewEvent();
	public static final Event AWAIT_EVENT_ERROR = Event.createNewEvent();

	private static final ExecutorService nonwaitBusSender = Executors.newCachedThreadPool();
	private static final ExecutorService awaitBusSenderListener = Executors.newCachedThreadPool();
	private static final Set<EventBusListener> registeredClassToSend = new HashSet<>();
	private static final ArrayList<ExecutorService> awaitBusSenderUnits=new ArrayList<>();

	public static final void registerListener(EventBusListener listener) {
		registeredClassToSend.add(listener);
	}
	
	public static final int addUnit(){
		awaitBusSenderUnits.add(Executors.newCachedThreadPool());
		return awaitBusSenderUnits.size() - 1;
	}

	public static final void removeListener(EventBusListener listener) {
		registeredClassToSend.remove(listener);
	}

	public static final void postEvent(Event e) {
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				nonwaitBusSender.execute(() -> linss.listen(e));
			}
		}
	}

	public static final void awaitPostEvent(Event e, EventBusListener source, int unit) {
		awaitPostEvent(e, source, 60, TimeUnit.SECONDS, unit);
	}

	public static final void awaitPostEvent(Event e, EventBusListener source, int awmax, TimeUnit tu, int unit) {
		int listens = 0;
		Set<Callable<Object>> tasks = new HashSet<>();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				tasks.add(() -> {
					linss.listen(e);
					return null;
				});
				listens++;
			}
		}
		if (listens == 0) {
			nonwaitBusSender.execute(() -> source.listen(AWAIT_EVENT_RUN_OVER));
		} else
			awaitBusSenderListener.execute(() -> {
				List<Future<Object>> os;
				try {
					os = awaitBusSenderUnits.get(unit).invokeAll(tasks, awmax, tu);
				} catch (Exception e1) {
					source.listen(AWAIT_EVENT_ERROR);
					return;
				}
				boolean over = true;
				for(Future<Object> f:os){
					if(!f.isDone()){
						over = false;
					}
				}
				if (over)
					source.listen(AWAIT_EVENT_RUN_OVER);
				else
					source.listen(AWAIT_EVENT_TIMEOUT);
			});
	}
}
