package com.github.nickid2018.chemistrylab.util;

import java.lang.reflect.*;
import org.apache.log4j.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.event.*;

public final class Ticker {

	public static final Logger logger = Logger.getLogger("Ticker");
	public static EventBus TICK_EVENT_BUS;

	// Initialize eventbus
	// Need no delay
	static {
		Class<?> clazz = EventBus.class;
		Constructor<?> con;
		try {
			Class<?> dispatcher = Class.forName("com.google.common.eventbus.Dispatcher");
			Method m = dispatcher.getDeclaredMethod("perThreadDispatchQueue");
			con = clazz.getDeclaredConstructor(String.class, Executor.class, dispatcher,
					SubscriberExceptionHandler.class);
			con.setAccessible(true);
			m.setAccessible(true);
			TICK_EVENT_BUS = (EventBus) con.newInstance("Tick-EventBus", Executors.newCachedThreadPool(),
					m.invoke(dispatcher), (SubscriberExceptionHandler) (e, c) -> {
						logger.error(message(c), e);
					});
		} catch (Throwable e) {
			logger.warn("Can't start the no-delay eventbus, system may run incorrectly in delayed eventbus.", e);
			TICK_EVENT_BUS = new EventBus("Tick-EventBus");
		}
	}
//
//	public static void main(String[] args) {
//		PropertyConfigurator.configure(Ticker.class.getResource("/assets/log4j.properties"));
//		Ticker.init();
//		TICK_EVENT_BUS.register(new Object() {
//			@Subscribe
//			public void onTick(TickerEvent e) {
//				if (Math.random() < 0.9) {
//					e.isSomeCancelled = true;
//					e.cancelledUnits++;
//				}
//			}
//		});
//		while (true) {
//		}
//	}

	// Atomic Operation
	private static AtomicBoolean stopped = new AtomicBoolean();

	private static long lastTicks;
	private static int ticks;
	private static int printTicks;
	private static TickerEvent lastEvent = new TickerEvent();
	private static int delayColdDown = 0;

	private static final javax.swing.Timer tick_sender = new javax.swing.Timer(39, e -> {
		if (lastEvent.isSomeCancelled) {
			if (delayColdDown % 40 == 0)
				logger.warn("Is something took too much time? A tick event has been cancelled by "
						+ lastEvent.cancelledUnits + " unit(s).");
			delayColdDown++;
		} else
			delayColdDown = delayColdDown == 0 ? 0 : delayColdDown - 1;
		TICK_EVENT_BUS.post(lastEvent = new TickerEvent());
		updateTick();
	});

	private Ticker() {
	}

	public static void init() {
		lastTicks = TimeUtils.getTime();
		tick_sender.start();
		logger.info("Ticker initialized.");
	}

	public static int getTicks() {
		return printTicks;
	}

	public static void stopTick() {
		tick_sender.stop();
		stopped.set(true);
		printTicks = 0;
	}

	public static void startTick() {
		stopped.set(false);
		tick_sender.start();
	}

	public static void setTickRate(int newRate) {
		tick_sender.stop();
		tick_sender.setDelay(1000 / newRate);
		tick_sender.start();
	}

	private static void updateTick() {
		if (TimeUtils.getTime() - lastTicks > 1000) {
			printTicks = ticks;
			ticks = 0;
			lastTicks = TimeUtils.getTime();
		}
		ticks++;
	}

	private static String message(SubscriberExceptionContext context) {
		Method method = context.getSubscriberMethod();
		return "Exception thrown by subscriber method " + method.getName() + '('
				+ method.getParameterTypes()[0].getName() + ')' + " on subscriber " + context.getSubscriber()
				+ " when dispatching event: " + context.getEvent();
	}
}
