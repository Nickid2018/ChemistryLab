package com.github.nickid2018.chemistrylab.util;

public class TimeUtils {

	/**
	 * Get the time in milliseconds
	 *
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return System.currentTimeMillis();
	}

	/**
	 * Get the time in nanoseconds
	 *
	 * @return The system time in nanoseconds
	 */
	public static long getNanoTime() {
		return System.nanoTime();
	}
}
