package com.github.nickid2018.chemistrylab.util;

import java.util.concurrent.*;

public class ThreadManger {

	private static final ExecutorService manager = Executors.newCachedThreadPool(r -> new Thread(r, "Thread Manager"));

	/**
	 * Invoke Task in concurrent thread.
	 * 
	 * @param r
	 */
	public static final void invoke(Runnable r) {
		manager.execute(r);
	}
}
