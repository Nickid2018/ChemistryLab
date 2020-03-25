package com.github.nickid2018.chemistrylab.render;

import java.util.*;
import java.nio.charset.*;
import org.lwjgl.opengl.*;
import com.alibaba.fastjson.*;
import org.apache.commons.io.*;
import java.util.concurrent.atomic.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.util.*;
import com.github.mmc1234.minigoldengine.texture.*;

public class AnimationTexture implements Cloneable {

	private int frames;
	private Texture[] id;
	private int[] delays;
	private int loopTime = 0;
	private Map<Object, Long> startTimes = new HashMap<>();
	private AtomicReference<Object> nowObj = new AtomicReference<>();

	public AnimationTexture(String ref) throws Exception {
		String descpf = ref + ".png.json";
		String text = IOUtils.toString(ResourceManager.getResourceAsStream(descpf), Charset.forName("GB2312"));
		JSONArray setts = JSON.parseArray(text);
		frames = setts.size();
		id = new Texture[frames];
		delays = new int[frames];
		for (int i = 0; i < frames; i++) {
			Texture texture = TextureLoader.getTexture(ref + "_" + i + ".png");
			id[i] = texture;
			loopTime += delays[i] = setts.getIntValue(i);
			int error = GL11.glGetError();
			if (error != GL11.GL_NO_ERROR) {
				for (int j = 0; j <= i; j++) {
					id[j].cleanup();
				}
				throw new Exception("#GL ERROR#" + error);
			}
		}
	}

	public AnimationTexture startToBind(Object o) {
		startTimes.put(o, TimeUtils.getTime());
		return this;
	}

	public void endToBind(Object o) {
		// Ensure no Null Pointers
		startTimes.replace(o, 0L);
	}

	public void preBind(Object o) {
		nowObj.set(o);
	}

	public void bind() {
		int rm = (int) ((TimeUtils.getTime() - startTimes.get(nowObj.get())) % loopTime);
		for (int i = 0; i < frames; i++) {
			rm -= delays[i];
			if (rm <= 0) {
				id[i].bind();
				break;
			}
		}
	}

	public int getFrameCount() {
		return frames;
	}

	public float getHeight() {
		return getHeight(0);
	}

	public float getHeight(int frame) {
		return id[frame].getHeight();
	}

	public float getWidth() {
		return getWidth(0);
	}

	public float getWidth(int frame) {
		return id[frame].getWidth();
	}

	public void release() {
		for (int i = 0; i < frames; i++) {
			id[i].cleanup();
		}
	}

	public int getTextureID() {
		return getTextureID(0);
	}

	public int getTextureID(int frame) {
		return id[frame].getID();
	}

	public AnimationTexture clone() {
		try {
			return (AnimationTexture) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
