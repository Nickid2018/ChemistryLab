package com.github.nickid2018.chemistrylab.util;

import org.apache.log4j.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.google.common.eventbus.*;
import com.google.common.util.concurrent.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.event.*;
import com.github.nickid2018.chemistrylab.init.MathHelper;
import com.github.nickid2018.chemistrylab.reaction.*;
import com.github.nickid2018.chemistrylab.reaction.data.Environment;

public final class Ticker {

	public static final Logger logger = Logger.getLogger("Ticker");
	public static final EventBus TICK_EVENT_BUS = new EventBus("Tick-EventBus");
	public static final ExecutorService service = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Ticker").build());

	public static void main(String[] args) {
		PropertyConfigurator.configure(ChemistryLab.class.getResourceAsStream("/assets/log4j.properties"));
		Ticker.init();
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info("Tick Rate: " + getTicks() + " Reaction Rate: " + reactionRate + " Counter: " + skipCounter
						+ " Normal: " + normalSkipCounter + " Delay: " + tick_sender.getDelay() + " MSPT: " + mspt);
			}
		}).start();
		TICK_EVENT_BUS.register(new Object() {
			boolean a = true;

			@Subscribe
			public void listenTick(TickerEvent e) throws Exception {
				if (a) {
					try {
						Thread.sleep(1000);
					} catch (Exception e1) {
					}
					a = false;
				}
			}
		});
	}

	// Atomic Operation
	private static AtomicBoolean stopped = new AtomicBoolean(false);

	private static long lastTicks;
	private static int ticks;
	private static int printTicks;
	private static int skipCounter = 0;
	private static int normalSkipCounter = 0;
	private static int nowNeedFix = 0;
	private static int fixTickRate = 8;
	private static int skipReactionCounter = 0;
	private static float reactionRate = 1;
	private static Future<?> future;
	private static Future<?> idlefuture;
	private static int tickRate = 40;
	private static javax.swing.Timer tick_sender = new javax.swing.Timer(tickRate, null);
	private static TickerEvent event = new TickerEvent();
	private static TickerIdleEvent idle = new TickerIdleEvent();
	private static float mspt;

	private Ticker() {
	}

	public static void init() {
		lastTicks = TimeUtils.getTime();
		tick_sender.addActionListener(e -> {
			if (idlefuture != null && idlefuture.isDone()) {
				idlefuture.cancel(true);
			}
			if (stopped.get()) {
				future.cancel(true);
				return;
			}
			if (future == null || future.isDone()) {
				event.isSkipReaction = false;
				event.skipTimes = 0;
				event.reactionRate = reactionRate * 0.04 * Environment.getSpeed();
				event.startTime = TimeUtils.getNanoTime();
				skipReactionCounter = 0;
				fixTickRate = 8;
				if (nowNeedFix == 0) {
					skipCounter = 0;
					normalSkipCounter = 0;
					reactionRate = 1;
					tick_sender.setDelay(tickRate);
				}
				future = service.submit(() -> {
					TICK_EVENT_BUS.post(event);
					long current = TimeUtils.getNanoTime();
					mspt = (float) MathHelper.eplison((current - event.startTime) / 1000_100.0, 2);
					idlefuture = service.submit(() -> {
						idle.startTime = current;
						TICK_EVENT_BUS.post(idle);
					});
				});
				if (nowNeedFix != 0) {
					nowNeedFix--;
				}
				updateTick();
			} else {
				// Skip This Tick
				skipCounter++;
				normalSkipCounter++;
				if (normalSkipCounter >= 100) {
					future.cancel(true);
					reactionRate = 1;
					normalSkipCounter = 0;
					skipCounter = 0;
					skipReactionCounter++;
					fixTickRate = 8;
					nowNeedFix = 0;
					event.isSkipReaction = true;
					event.skipTimes = skipReactionCounter * 100;
					event.reactionRate = 4 * Environment.getSpeed() * skipReactionCounter;
					event.startTime = TimeUtils.getNanoTime();
					// Need to record snapshot to restore data if possible
					future = service.submit(() -> TICK_EVENT_BUS.post(event));
					updateTick();
					tick_sender.setDelay(tickRate);
					logger.warn("Can't keep up! Skipped reaction during " + event.skipTimes + " ticks or "
							+ event.skipTimes / 25.0 + " seconds.");
					return;
				}
				if (normalSkipCounter % 20 == 0) {
					skipCounter /= 2;
					nowNeedFix = fixTickRate *= 4;
					reactionRate = normalSkipCounter / (float) skipCounter;
					logger.warn("Can't keep up! Skipped " + normalSkipCounter + " ticks or " + normalSkipCounter / 25.0
							+ " seconds.");
				}
				tick_sender.setDelay(tickRate * fixTickRate / (skipCounter + fixTickRate));
			}
		});
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
		tickRate = 1000 / newRate;
		if (skipCounter == 0)
			tick_sender.setDelay(tickRate);
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
}
