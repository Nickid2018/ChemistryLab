package com.github.nickid2018.chemistrylab.sound;

import java.nio.*;
import org.lwjgl.openal.*;

public abstract class Sound {

	protected int index;
	protected int source;
	protected int address;
	protected FloatBuffer sourcePos;
	protected FloatBuffer sourceVel;
	protected float length;

	public void setPosition(float x, float y, float z) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			sourcePos.put(index * 3 + 0, x);
			sourcePos.put(index * 3 + 1, y);
			sourcePos.put(index * 3 + 2, z);
			AL10.alSourcefv(source, AL10.AL_POSITION, (FloatBuffer) sourcePos.position(index * 3));
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void setPosition(float position) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			float position0 = position % length;
			AL10.alSourcef(source, AL11.AL_SEC_OFFSET, position0);
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void setVelocity(float x, float y, float z) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			sourceVel.put(index * 3 + 0, x);
			sourceVel.put(index * 3 + 1, y);
			sourceVel.put(index * 3 + 2, z);
			AL10.alSourcefv(source, AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void setLooping(boolean loop) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void setPitch(float pitch) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			AL10.alSourcef(source, AL10.AL_PITCH, pitch);
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void setGain(float gain) {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			AL10.alSourcef(source, AL10.AL_GAIN, gain);
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void playSound() {
		SoundSystem.addALFunction(() -> {
			AL10.alSourcePlay(source);
			SoundSystem.checkALError();
		});
	}

	public void pauseSound() {
		SoundSystem.addALFunction(() -> {
			AL10.alSourcePause(source);
			SoundSystem.checkALError();
		});
	}

	public void stopSound() {
		SoundSystem.addALFunction(() -> {
			AL10.alSourceStop(source);
			SoundSystem.checkALError();
		});
	}

	private int state;

	public void queryNowState() {
		SoundSystem.addALFunction(() -> {
			state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
			SoundSystem.checkALError();
		});
	}

	public int getNowState() {
		return state;
	}
}
