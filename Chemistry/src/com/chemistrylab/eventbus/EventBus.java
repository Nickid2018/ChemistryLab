package com.chemistrylab.eventbus;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * <p>
 * A bus of event.The class can post an event to all/some listeners to implement
 * something.
 * </p>
 * <p>
 * The functions in this class are all thread-safe.
 * </p>
 * <p>
 * <b>The Class will send event in concurrent thread!</b>
 * </p>
 * 
 * @author Nickid2018
 * @see #postEvent(Event)
 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
 */
public class EventBus {

	private static final ExecutorService nonwaitBusSender = Executors
			.newCachedThreadPool(new ThreadFact("Non-wait EventBus"));
	private static final ExecutorService awaitBusSenderListener = Executors
			.newCachedThreadPool(new ThreadFact("Await EventBus"));
	private static final Set<EventBusListener> registeredClassToSend = new HashSet<>();
	private static final ArrayList<ExecutorService> awaitBusSenderUnits = new ArrayList<>();
	private static final ReentrantLock unitLock = new ReentrantLock();
	private static final ReentrantLock sendLock = new ReentrantLock();
	private static final Map<UUID, Event> regedEvents = new TreeMap<>();
	private static final Map<Future<?>, Event> nonwaitEvents = new HashMap<>();

	/**
	 * An await progress will reply this when the event has been sent successfully.
	 */
	public static final Event AWAIT_EVENT_RUN_OVER = new AwaitEventSign("Await_Event_Run-over");

	/**
	 * An await progress will reply this when the event used too long time to send.
	 */
	public static final Event AWAIT_EVENT_TIMEOUT = new AwaitEventSign("Await_Event_Timeout");

	/**
	 * <p>
	 * An await progress will reply this when the event has occurred an error.
	 * </p>
	 * <p>
	 * Extra Value:
	 * </p>
	 * <ul>
	 * <li>0 - The error</li>
	 * </ul>
	 */
	public static final Event AWAIT_EVENT_ERROR = new AwaitEventSign("Await_Event_Error");

	/**
	 * EventBus has been shutdowned.
	 */
	public static final Event EVENTBUS_CLOSED = Event.createNewEvent("EventBus_Closed");

	/**
	 * Register an event.
	 * 
	 * @param e The event to register
	 */
	public static final void registerEvent(Event e) {
		regedEvents.put(e.eventId, e);
	}

	/**
	 * Remove an event.
	 * 
	 * @param e The event to remove
	 */
	public static final void removeEvent(Event e) {
		removeEvent(e.eventId);
	}

	/**
	 * Remove an event.
	 * 
	 * @param uuid The event to remove
	 */
	public static final void removeEvent(UUID uuid) {
		regedEvents.remove(uuid);
	}

	/**
	 * Find an event that has the UUID.
	 * 
	 * @param uuid The UUID of the event
	 * @return The event that has the UUID
	 * @throws NullPointerException when the event of the UUID have not been
	 *                              registered.
	 */
	public static final Event getEvent(UUID uuid) {
		return Objects.requireNonNull(regedEvents.get(uuid), "Unregistered event " + uuid).clone();
	}

	/**
	 * Get all registered events.
	 * 
	 * @return A collection of all registered events
	 */
	public static final Collection<Event> getRegisteredEvents() {
		return regedEvents.values();
	}

	/**
	 * Add an unit to send await-events.
	 * 
	 * @return Unit number
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 */
	public static final int addUnit() {
		return addUnit("Await Unit-" + awaitBusSenderUnits.size());
	}

	/**
	 * Add an unit to send await-events.
	 * 
	 * @param name The name of the unit
	 * @return Unit number
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 */
	public static final int addUnit(String name) {
		unitLock.lock();
		awaitBusSenderUnits.add(Executors.newCachedThreadPool(new ThreadFact(name)));
		unitLock.unlock();
		return awaitBusSenderUnits.size() - 1;
	}

	/**
	 * <p>
	 * Remove an unit.
	 * </p>
	 * <p>
	 * <b>Warning:</b>If an await eventbus will never be used, please remove it.
	 * </p>
	 * 
	 * @param unit Unit number
	 */
	public static final void removeUnit(int unit) {
		unitLock.lock();
		ExecutorService es = awaitBusSenderUnits.set(unit, null);
		unitLock.unlock();
		es.shutdownNow();
	}

	/**
	 * <p>
	 * Remove an unit and wait the progress over.
	 * </p>
	 * <p>
	 * <b>Warning:</b>If an await eventbus will never be used, please remove it.
	 * </p>
	 * 
	 * @param unit  Unit number
	 * @param awmax Max time to wait
	 * @param u     The unit of time
	 * @throws Exception when ExecutorService shutdowning meets an exception
	 * @return true if this executor terminated and false if the timeout elapsed
	 *         before termination
	 */
	public static final boolean awaitRemoveUnit(int unit, long awmax, TimeUnit u) throws Exception {
		unitLock.lock();
		ExecutorService es = awaitBusSenderUnits.get(unit);
		awaitBusSenderUnits.set(unit, null);
		unitLock.unlock();
		es.shutdown();
		return es.awaitTermination(awmax, u);
	}

	/**
	 * Register a listener to listen events
	 * 
	 * @param listener listener to register
	 */
	public static final void registerListener(EventBusListener listener) {
		sendLock.lock();
		registeredClassToSend.add(listener);
		sendLock.unlock();
	}

	/**
	 * Returns true if the listener has been registered.
	 * 
	 * @param listener The listener whose presence in this eventbus is to be tested
	 * @return true if the listener has been registered
	 */
	public static final boolean haveListener(EventBusListener listener) {
		return registeredClassToSend.contains(listener);
	}

	/**
	 * Remove the listener.
	 * 
	 * @param listener The listener to remove
	 * @throws IllegalArgumentException If the listener has not been registered.
	 */
	public static final void removeListener(EventBusListener listener) {
		if (!haveListener(listener))
			throw new IllegalArgumentException("Can't find listener " + listener);
		sendLock.lock();
		registeredClassToSend.remove(listener);
		sendLock.unlock();
	}

	/**
	 * Post an event.
	 * 
	 * @param e The event to post
	 */
	public static final void postEvent(Event e) {
		sendLock.lock();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				nonwaitEvents.put(nonwaitBusSender.submit(() -> linss.listen(e)), e);
			}
		}
		sendLock.unlock();
	}

	/**
	 * Post an event.
	 * 
	 * @param uuid The UUID of the event to post
	 */
	public static final void postEvent(UUID uuid) {
		Event e = regedEvents.get(uuid);
		if (e == null)
			throw new IllegalArgumentException("Unregistered event " + uuid);
		sendLock.lock();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				Event ev = e.clone();
				nonwaitEvents.put(nonwaitBusSender.submit(() -> linss.listen(ev)), ev);
			}
		}
		sendLock.unlock();
	}

	/**
	 * Post an event and wait the progress over.
	 * 
	 * @param e      The UUID of the event to post
	 * @param source The listener of the event monitor
	 * @param unit   Await Unit number
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 * @see #addUnit(String)
	 */
	public static final void awaitPostEvent(UUID e, EventBusListener source, int unit) {
		awaitPostEvent(getEvent(e), source, unit);
	}

	/**
	 * Post an event and wait the progress over.
	 * 
	 * @param e      The event to post
	 * @param source The listener of the event monitor
	 * @param unit   Await Unit number
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 * @see #addUnit(String)
	 */
	public static final void awaitPostEvent(Event e, EventBusListener source, int unit) {
		awaitPostEvent(e, source, 1, TimeUnit.MINUTES, unit);
	}

	/**
	 * Post an event and wait the progress over.
	 * 
	 * @param e      The UUID of the event to post
	 * @param source The listener of the event monitor
	 * @param awmax  Max time to wait
	 * @param tu     The unit of time
	 * @param unit   Await Unit number
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 * @see #addUnit(String)
	 */
	public static final void awaitPostEvent(UUID e, EventBusListener source, int awmax, TimeUnit tu, int unit) {
		awaitPostEvent(getEvent(e), source, awmax, tu, unit);
	}

	/**
	 * Post an event and wait the progress over.
	 * 
	 * @param e      The event to post
	 * @param source The listener of the event monitor
	 * @param awmax  Max time to wait
	 * @param tu     The unit of time
	 * @param unit   Await Unit number
	 * @see #addUnit(String)
	 */
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
			nonwaitEvents.put(nonwaitBusSender.submit(() -> source.listen(AWAIT_EVENT_RUN_OVER)), AWAIT_EVENT_RUN_OVER);
		} else
			awaitBusSenderListener.execute(() -> {
				List<Future<Object>> os;
				try {
					os = awaitBusSenderUnits.get(unit).invokeAll(tasks, awmax, tu);
				} catch (Throwable e1) {
					Event error = AWAIT_EVENT_ERROR.clone();
					error.putExtra(0, e1);
					source.listen(error);
					return;
				}
				boolean over = true;
				for (Future<Object> f : os) {
					try {
						f.get();
					} catch (Throwable e1) {
						if (e1 instanceof CancellationException) {
							over = false;
							break;
						}
						if (e1 instanceof ExecutionException)
							e1 = e1.getCause();
						Event error = AWAIT_EVENT_ERROR.clone();
						error.putExtra(0, e1);
						source.listen(error);
						return;
					}
				}
				if (over)
					source.listen(AWAIT_EVENT_RUN_OVER);
				else
					source.listen(AWAIT_EVENT_TIMEOUT);
			});
	}

	/**
	 * @deprecated The function cannot ensure the event sending progress will start
	 *             at the same time.The function is replaced by awaitPostEvent
	 * @param e an event
	 * @see #awaitPostEvent(Event, EventBusListener, int, TimeUnit, int)
	 */
	@Deprecated
	public static final void completelyAwait(Event e) {
		sendLock.lock();
		for (EventBusListener linss : registeredClassToSend) {
			if (linss.receiveEvents(e)) {
				linss.listen(e);
			}
		}
		sendLock.unlock();
	}

	public static final Map<Event.CompleteComparedEvent, Integer> getNowActiveEvents() {
		Map<Event.CompleteComparedEvent, Integer> ret = new TreeMap<>();
		for (Map.Entry<Future<?>, Event> en : nonwaitEvents.entrySet()) {
			if (en.getKey().isDone())
				continue;
			Event.CompleteComparedEvent e = new Event.CompleteComparedEvent(en.getValue());
			if (ret.containsKey(e)) {
				ret.replace(e, ret.get(e) + 1);
			} else {
				ret.put(e, 1);
			}
		}
		return ret;
	}

	/**
	 * Release all resource EventBus owned and shutdown all Bus.
	 */
	public static final void releaseEventBus() {
		postEvent(EVENTBUS_CLOSED);
		// Warning: Must stop ticking before pop the Non-wait EventBus
		Ticker.stopTick();
		nonwaitBusSender.shutdown();
		// Wait AWAIT EVENT OVER
		try {
			nonwaitBusSender.awaitTermination(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		for (ExecutorService es : awaitBusSenderUnits) {
			es.shutdownNow();
		}
		awaitBusSenderListener.shutdownNow();
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

	private static final class RecordCleaner implements EventBusListener {

		@Override
		public boolean receiveEvents(Event e) {
			return e.equals(Ticker.NEXT_TICK);
		}

		@Override
		public void listen(Event e) {
			if (((ThreadPoolExecutor) nonwaitBusSender).getActiveCount() == 0)
				nonwaitEvents.clear();
		}
	}

	private static final class AwaitEventSign extends Event {

		public AwaitEventSign(String name) {
			super(name);
			registerEvent(this);
		}

		@Override
		public void putExtra(int id, Object obj) {
		}

		@Override
		public void cancel() {
		}

		@Override
		public Event clone() {
			return this;
		}
	}

	static {
		registerListener(new RecordCleaner());
	}
}
