package com.chemistrylab.eventbus;

import java.util.concurrent.*;

public class ThreadManger {

	private static final ExecutorService manager = Executors.newCachedThreadPool(r -> new Thread(r, "Thread Manager"));
	
	public static final void invoke(Runnable r){
		manager.execute(r);
	}
}
