package com.chemistrylab.eventbus;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class EventBus {

	public static final Event AWAIT_EVENT_RUN_OVER = Event.createNewEvent();
	public static final Event AWAIT_EVENT_TIMEOUT = Event.createNewEvent();
	public static final Event AWAIT_EVENT_ERROR = Event.createNewEvent();

	private static final ExecutorService nonwaitBusSender = Executors
			.newCachedThreadPool(new ThreadFact("Non-wait EventBus"));
	private static final ExecutorService awaitBusSenderListener = Executors
			.newCachedThreadPool(new ThreadFact("Await EventBus"));
	private static final Set<EventBusListener> registeredClassToSend = new HashSet<>();
	private static final ArrayList<ExecutorService> awaitBusSenderUnits = new ArrayList<>();
	private static final ReentrantLock unitLock = new ReentrantLock();
	private static final ReentrantLock sendLock = new ReentrantLock();
	protected static final ArrayList<Event> regedEvents = new ArrayList<>();

	public static final int addUnit() {
		unitLock.lock();
		awaitBusSenderUnits
				.add(Executors.newCachedThreadPool(new ThreadFact("Await Unit-" + awaitBusSenderUnits.size())));
		unitLock.unlock();
		return awaitBusSenderUnits.size() - 1;
	}

	public static final int addUnit(String name) {
		unitLock.lock();
		awaitBusSenderUnits.add(Executors.newCachedThreadPool(new ThreadFact(name)));
		unitLock.unlock();
		return awaitBusSenderUnits.size() - 1;
	}

	public static final void removeUnit(int unit) {
		unitLock.lock();
		ExecutorService es = awaitBusSenderUnits.get(unit);
		awaitBusSenderUnits.remove(unit);
		awaitBusSenderUnits.add(unit, null);
		unitLock.unlock();
		es.shutdownNow();
	}

	public static final void registerListener(EventBusListener listener) {
		sendLock.lock();
		registeredClassToSend.add(listener);
		sendLock.unlock();
	}

	public static final boolean haveListener(EventBusListener listener) {
		sendLock.lock();
		boolean ret = registeredClassToSend.contains(listener);
		sendLock.unlock();
		return ret;
	}

	public static final void removeListener(EventBusListener listener) {
		if (!haveListener(listener))
			throw new IllegalArgumentException("Can't find listener " + listener);
		sendLock.lock();
		registeredClassToSend.remove(listener);
		sendLock.unlock();
	}

	public static final void postEvent(Event e) {
		sendLock.lock();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				nonwaitBusSender.execute(() -> linss.listen(e));
			}
		}
		sendLock.unlock();
	}

	public static final void awaitPostEvent(Event e, EventBusListener source, int unit) {
		awaitPostEvent(e, source, 60, TimeUnit.SECONDS, unit);
	}

	public static final void awaitPostEvent(Event e, EventBusListener source, int awmax, TimeUnit tu, int unit) {
		int listens = 0;
		Set<Callable<Object>> tasks = new HashSet<>();
		sendLock.lock();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				tasks.add(() -> {
					linss.listen(e);
					return null;
				});
				listens++;
			}
		}
		sendLock.unlock();
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
				for (Future<Object> f : os) {
					if (!f.isDone()) {
						over = false;
					}
				}
				if (over)
					source.listen(AWAIT_EVENT_RUN_OVER);
				else
					source.listen(AWAIT_EVENT_TIMEOUT);
			});
	}

	public static final int getNonawaitSize() {
		return ((ThreadPoolExecutor) nonwaitBusSender).getActiveCount();
	}

	public static final long getPassedNonwaitEvents() {
		return ((ThreadPoolExecutor) nonwaitBusSender).getCompletedTaskCount();
	}

	public static final int getAvailableAwaitUnits() {
		return awaitBusSenderUnits.size();
	}

	private static final class ThreadFact implements ThreadFactory {

		private String name;

		public ThreadFact(String n) {
			name = n;
		}

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, name);
		}

	}
}
