package com.chemistrylab.sound;

import java.io.*;
import java.nio.*;
import org.lwjgl.openal.*;
import javax.sound.sampled.*;

public final class WaveSound extends Sound {

	private WaveData waveFile;

	public WaveSound(String file) throws UnsupportedAudioFileException, IOException {
		waveFile = WaveData.create(file);
		index = SoundSystem.newSound();
		address = SoundSystem.getBuffer(index);
		source = SoundSystem.getSource(index);
		sourcePos = SoundSystem.getSourcePositionBuffer();
		sourceVel = SoundSystem.getSourceVelocityBuffer();
		SoundSystem.addALFunction(this::initALSound);
	}

	private void initALSound() {
		AL10.alBufferData(address, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		int bytes = AL10.alGetBufferi(address, AL10.AL_SIZE);
		int bits = AL10.alGetBufferi(address, AL10.AL_BITS);
		int channels = AL10.alGetBufferi(address, AL10.AL_CHANNELS);
		int freq = AL10.alGetBufferi(address, AL10.AL_FREQUENCY);
		int samples = bytes / (bits / 8);
		length = (samples / (float) freq) / channels;
		AL10.alSourcei(source, AL10.AL_BUFFER, address);
		AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
		AL10.alSourcefv(source, AL10.AL_POSITION, (FloatBuffer) sourcePos.position(index * 3));
		AL10.alSourcefv(source, AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
		AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
		SoundSystem.checkALError();
	}
}
