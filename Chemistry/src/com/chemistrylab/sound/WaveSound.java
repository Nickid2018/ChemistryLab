package com.chemistrylab.sound;

import java.io.*;
import java.nio.*;
import org.lwjgl.openal.*;
import javax.sound.sampled.*;

public final class WaveSound {

	private WaveData waveFile;
	private int index;
	private int source;
	private int address;
	private FloatBuffer sourcePos;
	private FloatBuffer sourceVel;
	private boolean lastCommandOver = true;

	public WaveSound(String file) throws UnsupportedAudioFileException, IOException {
		waveFile = WaveData.create(file);
		index = SoundSystem.newSound();
		address = SoundSystem.getBuffer(index);
		source = SoundSystem.getSource(index);
		sourcePos = SoundSystem.getSourcePositionBuffer();
		sourceVel = SoundSystem.getSourceVelocityBuffer();
		lastCommandOver = false;
		SoundSystem.addALFunction(this::initALSound);
	}

	private void initALSound() {
		AL10.alBufferData(address, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		AL10.alSourcei(source, AL10.AL_BUFFER, address);
		AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
		AL10.alSource(source, AL10.AL_POSITION, (FloatBuffer) sourcePos.position(index * 3));
		AL10.alSource(source, AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
		AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
		SoundSystem.checkALError();
		lastCommandOver = true;
	}

	private void checkLastCommandOver() {
		if (!lastCommandOver)
			throw new IllegalStateException("Last Operation is not over.");
	}

	public void setPosition(float x, float y, float z) {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			sourcePos.put(index * 3 + 0, x);
			sourcePos.put(index * 3 + 1, y);
			sourcePos.put(index * 3 + 2, z);
			AL10.alSource(source, AL10.AL_POSITION, (FloatBuffer) sourcePos.position(index * 3));
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void setVelocity(float x, float y, float z) {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			sourceVel.put(index * 3 + 0, x);
			sourceVel.put(index * 3 + 1, y);
			sourceVel.put(index * 3 + 2, z);
			AL10.alSource(source, AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void setLooping(boolean loop) {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void setPitch(float pitch) {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourcef(source, AL10.AL_PITCH, pitch);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void setGain(float gain) {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourcef(source, AL10.AL_GAIN, gain);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void playSound() {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void pauseSound() {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourcePause(source);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}

	public void stopSound() {
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}
	
	private int state;
	public void queryNowState(){
		checkLastCommandOver();
		lastCommandOver = false;
		SoundSystem.addALFunction(() -> {
			state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
			SoundSystem.checkALError();
			lastCommandOver = true;
		});
	}
	
	public int getNowState(){
		return state;
	}
}
