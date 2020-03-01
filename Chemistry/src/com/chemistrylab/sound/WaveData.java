package com.chemistrylab.sound;

import java.io.*;
import java.net.*;
import java.nio.*;
import org.lwjgl.openal.*;
import javax.sound.sampled.*;
import com.chemistrylab.util.*;

/**
 *
 * Utitlity class for loading wavefiles.
 *
 */
public class WaveData {
	/** actual wave data */
	public final ByteBuffer data;

	/** format type of data */
	public final int format;

	/** sample rate of data */
	public final int samplerate;

	/**
	 * Creates a new WaveData
	 * 
	 * @param data       actual wavedata
	 * @param format     format of wave data
	 * @param samplerate sample rate of data
	 */
	private WaveData(ByteBuffer data, int format, int samplerate) {
		this.data = data;
		this.format = format;
		this.samplerate = samplerate;
	}

	/**
	 * Disposes the wavedata
	 */
	public void dispose() {
		data.clear();
	}

	/**
	 * Creates a WaveData container from the specified url
	 * 
	 * @param path URL to file
	 * @return WaveData containing data, or null if a failure occured
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static WaveData create(URL path) throws UnsupportedAudioFileException, IOException {
		return create(AudioSystem.getAudioInputStream(new BufferedInputStream(path.openStream())));
	}

	/**
	 * Creates a WaveData container from the specified in the classpath
	 * 
	 * @param path path to file (relative, and in classpath)
	 * @return WaveData containing data, or null if a failure occured
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static WaveData create(String path) throws UnsupportedAudioFileException, IOException {
		return create(ResourceManager.getResource(path));
	}

	/**
	 * Creates a WaveData container from the specified inputstream
	 * 
	 * @param is InputStream to read from
	 * @return WaveData containing data, or null if a failure occured
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static WaveData create(InputStream is) throws UnsupportedAudioFileException, IOException {
		return create(AudioSystem.getAudioInputStream(is));
	}

	/**
	 * Creates a WaveData container from the specified bytes
	 *
	 * @param buffer array of bytes containing the complete wave file
	 * @return WaveData containing data, or null if a failure occured
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static WaveData create(byte[] buffer) throws UnsupportedAudioFileException, IOException {
		return create(AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer))));
	}

	/**
	 * Creates a WaveData container from the specified ByetBuffer. If the buffer is
	 * backed by an array, it will be used directly, else the contents of the buffer
	 * will be copied using get(byte[]).
	 *
	 * @param buffer ByteBuffer containing sound file
	 * @return WaveData containing data, or null if a failure occured
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static WaveData create(ByteBuffer buffer) throws UnsupportedAudioFileException, IOException {
		byte[] bytes = null;

		if (buffer.hasArray()) {
			bytes = buffer.array();
		} else {
			bytes = new byte[buffer.capacity()];
			buffer.get(bytes);
		}
		return create(bytes);
	}

	/**
	 * Creates a WaveData container from the specified stream
	 * 
	 * @param ais AudioInputStream to read from
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(AudioInputStream ais) {
		// get format of data
		AudioFormat audioformat = ais.getFormat();

		// get channels
		int channels = 0;
		if (audioformat.getChannels() == 1) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_MONO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_MONO16;
			} else {
				throw new RuntimeException("Illegal sample size");
			}
		} else if (audioformat.getChannels() == 2) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_STEREO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_STEREO16;
			} else {
				throw new RuntimeException("Illegal sample size");
			}
		} else {
			throw new RuntimeException("Only mono or stereo is supported");
		}

		// read data into buffer
		byte[] buf = new byte[audioformat.getChannels() * (int) ais.getFrameLength() * audioformat.getSampleSizeInBits()
				/ 8];
		int read = 0, total = 0;
		try {
			while ((read = ais.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
				total += read;
			}
		} catch (IOException ioe) {
			return null;
		}

		// insert data into bytebuffer
		ByteBuffer buffer = convertAudioBytes(buf, audioformat.getSampleSizeInBits() == 16);
		/*
		 * ByteBuffer buffer = ByteBuffer.allocateDirect(buf.length); buffer.put(buf);
		 * buffer.rewind();
		 */

		// create our result
		WaveData wavedata = new WaveData(buffer, channels, (int) audioformat.getSampleRate());

		// close stream
		try {
			ais.close();
		} catch (IOException ioe) {
		}

		return wavedata;
	}

	/**
	 * Convert the audio bytes into the stream
	 * 
	 * @param audio_bytes    The audio byts
	 * @param two_bytes_data True if we using double byte data
	 * @return The byte bufer of data
	 */
	private static ByteBuffer convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data) {
		ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
		dest.order(ByteOrder.nativeOrder());
		ByteBuffer src = ByteBuffer.wrap(audio_bytes);
		src.order(ByteOrder.LITTLE_ENDIAN);
		if (two_bytes_data) {
			ShortBuffer dest_short = dest.asShortBuffer();
			ShortBuffer src_short = src.asShortBuffer();
			while (src_short.hasRemaining())
				dest_short.put(src_short.get());
		} else {
			while (src.hasRemaining())
				dest.put(src.get());
		}
		dest.rewind();
		return dest;
	}
}
