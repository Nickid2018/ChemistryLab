package com.chemistrylab;

import java.util.*;
import java.util.concurrent.*;

public final class DebugSystem {

	public static int maxstoreinfos 									= 		 150;
	private static final Queue<Integer> storedFPS 		=		 new LinkedBlockingQueue<>(maxstoreinfos);
	private static final Queue<Long> storedMemory 	=		 new LinkedBlockingQueue<>(maxstoreinfos);

	public static final void addFPSInfo(int fpsin) {
		if (storedFPS.size() == maxstoreinfos) {
			storedFPS.poll();
		}
		storedFPS.offer(fpsin);
	}

	public static final void addMemInfo(long l) {
		if (storedMemory.size() == maxstoreinfos) {
			storedMemory.poll();
		}
		storedMemory.offer(l);
	}

	public static final Queue<Integer> getFPSs() {
		return storedFPS;
	}

	public static final Queue<Long> getMems() {
		return storedMemory;
	}
}
