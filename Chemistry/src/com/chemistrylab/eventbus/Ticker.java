package com.chemistrylab.eventbus;

import javax.swing.*;
import org.apache.log4j.*;
import com.chemistrylab.*;
import java.util.concurrent.*;

public final class Ticker implements EventBusListener {

	public static final Event NEXT_TICK = Event.createNewEvent();

	public static final Logger logger = Logger.getLogger("Ticker");

	private static final Ticker tickerInstance = new Ticker();
	
	private static boolean lastSendOver = true;
	private static long lastTicks;
	private static int ticks;
	private static int printTicks;

	private static final Timer tick_sender = new Timer(40, (e) -> {
		if (lastSendOver == true) {
			EventBus.awaitPostEvent(NEXT_TICK, tickerInstance, 2, TimeUnit.SECONDS);
			lastSendOver = false;
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
			logger.warn("Tick update error!");
		} else if (e.equals(EventBus.AWAIT_EVENT_TIMEOUT)) {
			logger.warn("Tick lost!");
		}
		//Sync
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {}
		lastSendOver = true;
		updateTick();
	}

	public static void init() {
		lastTicks = ChemistryLab.getTime();
	}

	public static int getTicks() {
		return printTicks;
	}

	private static void updateTick() {
		if (ChemistryLab.getTime() - lastTicks > 1000) {
			printTicks = ticks;
			ticks = 0;
			lastTicks = ChemistryLab.getTime();
		}
		ticks++;
	}

	static {
		EventBus.registerListener(tickerInstance);
		tick_sender.start();
		logger.info("Ticker initialized.");
	}
}
