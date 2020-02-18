package com.chemistrylab.sound;

import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.openal.*;
import org.lwjgl.system.*;
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
	 * Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	 */
	private static final FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6)
			.put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });

	private static final class SoundThread implements Runnable {

		private Queue<Runnable> queue = new LinkedBlockingQueue<>();
		private boolean stop = false;
		private long device;
		private long context;
		private long start_stop;
		private String alcVersion;
		private String alVersion;

		@Override
		public void run() {
			logger.info("Start loading Sound System");
			device = ALC10.alcOpenDevice((ByteBuffer) null);
			if (device == MemoryUtil.NULL) {
				logger.error("Failed to open the default OpenAL device.System will run in slient mode.");
				return;
			}
			ALCCapabilities deviceCaps = ALC.createCapabilities(device);
			context = ALC10.alcCreateContext(device, (IntBuffer) null);
			if (context == MemoryUtil.NULL) {
				ALC10.alcCloseDevice(device);
				logger.error("Failed to create OpenAL context.System will run in slient mode.");
				return;
			}
			ALC10.alcMakeContextCurrent(context);
			AL.createCapabilities(deviceCaps);
			logger.info("OpenAL version:" + AL10.alGetString(AL10.AL_VERSION));
			listenerPos.flip();
			listenerVel.flip();
			listenerOri.flip();
			AL10.alGenBuffers(buffer);
			AL10.alGenSources(source);
			AL10.alListenerfv(AL10.AL_POSITION, listenerPos);
			AL10.alListenerfv(AL10.AL_VELOCITY, listenerVel);
			AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOri);
			logger.info("Loaded Sound System");
			alcVersion = ALC10.alcGetInteger(device, ALC10.ALC_MAJOR_VERSION) + "."
					+ ALC10.alcGetInteger(device, ALC10.ALC_MINOR_VERSION);
			alVersion = AL11.alGetString(AL11.AL_VERSION);
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
			ALC10.alcDestroyContext(context);
			ALC10.alcCloseDevice(device);
			logger.info("Sound System closed, used " + (System.currentTimeMillis() - start_stop) + " milliseconds.");
		}

	}

	private static SoundThread INSTANCE = new SoundThread();
	private static Thread THREAD_INSTANCE;

	private static int index = 0;

	public static final void init() {
		(THREAD_INSTANCE = new Thread(INSTANCE, "Sound System")).start();
	}

	public static final String getALVersion() {
		return INSTANCE.alVersion;
	}

	public static final String getALCVersion() {
		return INSTANCE.alcVersion;
	}

	public static final void stopProgram() {
		INSTANCE.stop = true;
		INSTANCE.start_stop = System.currentTimeMillis();
	}

	public static boolean isAlive() {
		return THREAD_INSTANCE.isAlive();
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
