package com.chemistrylab.eventbus;

import java.util.*;
import java.util.concurrent.*;

public class EventBus {

	public static final Event AWAIT_EVENT_RUN_OVER = Event.createNewEvent();
	public static final Event AWAIT_EVENT_TIMEOUT = Event.createNewEvent();
	public static final Event AWAIT_EVENT_ERROR = Event.createNewEvent();

	private static final ExecutorService nonwaitBusSender = Executors.newCachedThreadPool();
	private static final ExecutorService awaitBusSenderListener = Executors.newCachedThreadPool();
	private static final Map<Integer, ExecutorService> awaitBusSenders = new HashMap<>();
	private static final Set<EventBusListener> registeredClassToSend = new HashSet<>();

	public static final void registerListener(EventBusListener listener) {
		registeredClassToSend.add(listener);
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

	public static final void awaitPostEvent(Event e, EventBusListener source) {
		awaitPostEvent(e, source, 60, TimeUnit.SECONDS);
	}

	public static final void awaitPostEvent(Event e, EventBusListener source, int awmax, TimeUnit tu) {
		int awid = (int) (Math.random() * Integer.MAX_VALUE);
		ExecutorService es = Executors.newCachedThreadPool();
		awaitBusSenders.put(awid, es);
		int listens = 0;
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				es.execute(() -> linss.listen(e));
				listens++;
			}
		}
		if (listens == 0) {
			nonwaitBusSender.execute(() -> source.listen(AWAIT_EVENT_RUN_OVER));
			awaitBusSenders.remove(awid);
		} else
			awaitBusSenderListener.execute(() -> {
				boolean over;
				try {
					awaitBusSenders.get(awid).shutdown();
					over = awaitBusSenders.get(awid).awaitTermination(awmax, tu);
				} catch (Exception e1) {
					source.listen(AWAIT_EVENT_ERROR);
					awaitBusSenders.remove(awid);
					return;
				}
				if (over)
					source.listen(AWAIT_EVENT_RUN_OVER);
				else
					source.listen(AWAIT_EVENT_TIMEOUT);
				awaitBusSenders.remove(awid);
			});
	}
}
