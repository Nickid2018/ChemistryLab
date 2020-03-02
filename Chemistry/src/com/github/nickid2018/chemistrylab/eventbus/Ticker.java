package com.github.nickid2018.chemistrylab.eventbus;

import javax.swing.*;
import org.apache.log4j.*;

import com.github.nickid2018.chemistrylab.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public final class Ticker implements EventBusListener {

	public static final Event NEXT_TICK = Event.createNewEvent("Next_Tick");

	public static final Logger logger = Logger.getLogger("Ticker");

	private static final Ticker tickerInstance = new Ticker();
	private static final int EVENTBUS_UNIT = EventBus.addUnit("Ticker");

	// Atomic Operation
	private static AtomicBoolean lastSendOver = new AtomicBoolean(true);
	private static AtomicBoolean stopped = new AtomicBoolean();

	private static long lastTicks;
	private static int ticks;
	private static int printTicks;

	private static final Timer tick_sender = new Timer(40, (e) -> {
		if (lastSendOver.get()) {
			// The timeout check will use 40ms--1 tick
			// Actually, this is the tick length
			EventBus.awaitPostEvent(NEXT_TICK, tickerInstance, 1960, TimeUnit.MILLISECONDS, EVENTBUS_UNIT);
			lastSendOver.set(false);
		}
	});

	private Ticker() {
	}

	@Override
	public boolean receiveEvents(Event e) {
		return e.equals(EventBus.AWAIT_EVENT_RUN_OVER) || e.equals(EventBus.AWAIT_EVENT_ERROR)
				|| e.equals(EventBus.AWAIT_EVENT_TIMEOUT);
	}

	@Override
	public void listen(Event e) {
		if (e.equals(EventBus.AWAIT_EVENT_RUN_OVER)) {
		} else if (e.equals(EventBus.AWAIT_EVENT_ERROR)) {
			logger.warn("Tick update error!", (Throwable) e.getExtra(0));
		} else if (e.equals(EventBus.AWAIT_EVENT_TIMEOUT)) {
			logger.warn("A tick update used too long!");
		} else {
			stopped.lazySet(true);
			tick_sender.stop();
			return;
		}
		// Sync
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
		}
		lastSendOver.set(true && !stopped.get());
		updateTick();
	}

	public static void init() {
		lastTicks = ChemistryLab.getTime();
		EventBus.registerListener(tickerInstance);
		tick_sender.start();
		logger.info("Ticker initialized.");
	}

	public static int getTicks() {
		return printTicks;
	}

	public static void stopTick() {
		tick_sender.stop();
		lastSendOver.set(false);
		stopped.set(true);
		printTicks = 0;
	}

	public static void startTick() {
		lastSendOver.set(true);
		stopped.set(false);
		tick_sender.start();
	}

	private static void updateTick() {
		if (ChemistryLab.getTime() - lastTicks > 1000) {
			printTicks = ticks;
			ticks = 0;
			lastTicks = ChemistryLab.getTime();
		}
		ticks++;
	}
}
