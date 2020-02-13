package com.chemistrylab.sound;

import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.openal.*;
import org.apache.log4j.*;
import java.util.concurrent.*;

public class SoundSystem {

	public static final Logger logger = Logger.getLogger("Sound System");

	/** Maximum data buffers we will need. */
	public static final int NUM_BUFFERS = 127;

	/** Maximum emissions we will need. */
	public static final int NUM_SOURCES = 127;

	/** Buffers hold sound data. */
	private static final IntBuffer buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);

	/** Sources are points emitting sound. */
	private static final IntBuffer source = BufferUtils.createIntBuffer(NUM_SOURCES);

	private static boolean[] using = new boolean[NUM_BUFFERS];

	/** Position of the source sound. */
	private static final FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3 * NUM_SOURCES);

	/** Velocity of the source sound. */
	private static final FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3 * NUM_SOURCES);

	/** Position of the listener. */
	private static final FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3)
			.put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the listener. */
	private static final FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3)
			.put(new float[] { 0.0f, 0.0f, 0.0f });

	/**
	 * Orientation of the listener. (first 3 elements are "at", second 3 are
	 * "up")
	 */
	private static final FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6)
			.put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });

	private static final class SoundThread implements Runnable {

		private Queue<Runnable> queue = new LinkedBlockingQueue<>();
		private boolean stop = false;

		@Override
		public void run() {
			logger.info("Start loading Sound System");
			try {
				AL.create();
				logger.info("OpenAL version:" + AL10.alGetString(AL10.AL_VERSION));
				listenerPos.flip();
				listenerVel.flip();
				listenerOri.flip();
				AL10.alGenBuffers(buffer);
				AL10.alGenSources(source);
				AL10.alListener(AL10.AL_POSITION, listenerPos);
				AL10.alListener(AL10.AL_VELOCITY, listenerVel);
				AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
			} catch (LWJGLException e) {
				logger.error("Can't create AL, program will be started in slient mode.", e);
				return;
			}
			logger.info("Loaded Sound System");
			// Main Loop of Sound System
			while (!stop) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				while (!queue.isEmpty()) {
					queue.poll().run();
					checkALError();
				}
			}
			AL10.alDeleteSources(source);
			AL10.alDeleteBuffers(buffer);
			checkALError();
			AL.destroy();
		}

	}

	private static SoundThread INSTANCE = new SoundThread();

	private static int index = 0;

	public static final void init() {
		new Thread(INSTANCE, "Sound System").start();
	}
	
	public static final void stopProgram(){
		INSTANCE.stop = true;
	}

	public static final synchronized int newSound() {
		index = 0;
		for (; index < NUM_BUFFERS; index++) {
			if (!using[index])
				break;
		}
		if (index > NUM_BUFFERS)
			throw new RuntimeException("Can't create new sound!");
		using[index] = true;
		return index;
	}

	public static final int getSource(int index) {
		return source.get(index);
	}

	public static final int getBuffer(int index) {
		return buffer.get(index);
	}

	public static final FloatBuffer getSourcePositionBuffer() {
		return sourcePos;
	}

	public static final FloatBuffer getSourceVelocityBuffer() {
		return sourceVel;
	}

	public static void addALFunction(Runnable r) {
		INSTANCE.queue.offer(r);
	}

	public static void checkALError() {
		int ret = AL10.alGetError();
		if (ret != AL10.AL_NO_ERROR) {
			String error = "";
			switch (ret) {
			case AL10.AL_INVALID_NAME:
				error = "Invalid name(" + ret + ")";
				break;
			case AL10.AL_INVALID_ENUM:
				error = "Invalid enum(" + ret + ")";
				break;
			case AL10.AL_INVALID_OPERATION:
				error = "Invalid operation(" + ret + ")";
				break;
			case AL10.AL_INVALID_VALUE:
				error = "Invalid value(" + ret + ")";
				break;
			case AL10.AL_OUT_OF_MEMORY:
				error = "Out of memory(" + ret + ")";
				break;
			}
			logger.error("#AL ERROR#" + error);
		}
	}
}