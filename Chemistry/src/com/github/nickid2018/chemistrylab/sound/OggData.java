package com.github.nickid2018.chemistrylab.sound;

import java.io.*;
import java.nio.*;

/**
 * Data describing the sounds in a OGG file
 */
public class OggData {
	/** The data that has been read from the OGG file */
	public ByteBuffer data;
	/** The sampling rate */
	public int samplerate;
	/** The number of channels in the sound file */
	public int channels;

	public void dispose() {
		data.clear();
	}

	/**
	 * Get the data out of an OGG file
	 * 
	 * @param input The input stream from which to read the OGG file
	 * @return The data describing the OGG thats been read
	 * @throws IOException Indicaites a failure to read the OGG file
	 */
	public static OggData getData(InputStream input) throws IOException {
		if (input == null) {
			throw new IOException("Failed to read OGG, source does not exist?");
		}
		ByteArrayOutputStream dataout = new ByteArrayOutputStream();
		OggInputStream oggInput = new OggInputStream(input);
		while (!oggInput.atEnd()) {
			dataout.write(oggInput.read());
		}

		OggData ogg = new OggData();
		ogg.channels = oggInput.getChannels();
		ogg.samplerate = oggInput.getRate();
		oggInput.close();

		byte[] data = dataout.toByteArray();
		ogg.data = ByteBuffer.allocateDirect(data.length);
		ogg.data.put(data);
		ogg.data.rewind();

		return ogg;
	}
}
