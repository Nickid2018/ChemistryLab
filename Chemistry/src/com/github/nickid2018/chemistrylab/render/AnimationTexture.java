package com.github.nickid2018.chemistrylab.render;

import java.io.*;
import java.util.*;
import org.lwjgl.opengl.*;
import com.alibaba.fastjson.*;
import java.util.concurrent.atomic.*;
import com.github.nickid2018.chemistrylab.*;
import com.github.nickid2018.chemistrylab.util.*;

public class AnimationTexture implements Cloneable {

	private String ref;
	private int frames;
//	private Texture[] id;
	private int[] delays;
	private int loopTime = 0;
	private Map<Object, Long> startTimes = new HashMap<>();
	private AtomicReference<Object> nowObj = new AtomicReference<>();

	public AnimationTexture(String ref) throws Exception {
		this.ref = ref;
		String descpf = ref + ".png.json";
		InputStream is = ResourceManager.getResourceAsStream(descpf);
		InputStreamReader reader = new InputStreamReader(is, "GB2312");
		char[] ch = new char[1];
		String text = "";
		while ((reader.read(ch)) > -1) {
			text += new String(ch);
		}
		text.trim();
		JSONArray setts = JSON.parseArray(text);
		frames = setts.size();
//		id = new Texture[frames];
//		delays = new int[frames];
//		for (int i = 0; i < frames; i++) {
//			Texture texture = org.newdawn.slick.opengl.TextureLoader.getTexture("PNG",
//					ResourceManager.getResourceAsStream(ref + "_" + i + ".png"), GL11.GL_LINEAR);
//			id[i] = texture;
//			loopTime += delays[i] = setts.getIntValue(i);
//			int error = GL11.glGetError();
//			if (error != GL11.GL_NO_ERROR) {
//				for (int j = 0; j <= i; j++) {
//					id[j].release();
//				}
//				throw new Exception("#GL ERROR#" + error);
//			}
//		}
	}

//	public boolean hasAlpha() {
//		return hasAlpha(0);
//	}

//	public boolean hasAlpha(int frame) {
//		return id[frame].hasAlpha();
//	}

//	public Texture startToBind(Object o) {
//		startTimes.put(o, ChemistryLab.getTime());
//		return this;
//	}

	public void endToBind(Object o) {
		// Ensure no Null Pointers
		startTimes.replace(o, 0L);
	}

	public void preBind(Object o) {
		nowObj.set(o);
	}

	public void bind() {
		int rm = (int) ((ChemistryLab.getTime() - startTimes.get(nowObj.get())) % loopTime);
		for (int i = 0; i < frames; i++) {
			rm -= delays[i];
			if (rm <= 0) {
//				id[i].bind();
				break;
			}
		}
	}

	public int getFrameCount() {
		return frames;
	}

//	public int getImageHeight() {
//		return getImageHeight(0);
//	}

//	public int getImageHeight(int frame) {
//		return id[frame].getImageHeight();
//	}

//	public int getImageWidth() {
//		return getImageWidth(0);
//	}
//
//	public int getImageWidth(int frame) {
//		return id[frame].getImageWidth();
//	}

//	public float getHeight() {
//		return getHeight(0);
//	}
//
//	public float getHeight(int frame) {
//		return id[frame].getHeight();
//	}
//
//	public float getWidth() {
//		return getWidth(0);
//	}
//
//	public float getWidth(int frame) {
//		return id[frame].getWidth();
//	}

//	public int getTextureHeight() {
//		return getTextureHeight(0);
//	}
//
//	public int getTextureHeight(int frame) {
//		return id[frame].getTextureHeight();
//	}
//
//	public int getTextureWidth() {
//		return getTextureWidth(0);
//	}
//
//	public int getTextureWidth(int frame) {
//		return id[frame].getTextureWidth();
//	}

//	public void release() {
//		for (int i = 0; i < frames; i++) {
//			id[i].release();
//		}
//	}

//	public int getTextureID() {
//		return getTextureID(0);
//	}
//
//	public int getTextureID(int frame) {
//		return id[frame].getTextureID();
//	}

//	public byte[] getTextureData() {
//		return getTextureData(0);
//	}
//
//	public byte[] getTextureData(int frame) {
//		return id[frame].getTextureData();
//	}
//
//	@Override
//	public void setTextureFilter(int textureFilter) {
//		setTextureFilter(textureFilter, 0);
//	}
//
//	public void setTextureFilter(int textureFilter, int frame) {
//		id[frame].setTextureFilter(textureFilter);
//	}

	public AnimationTexture clone() {
		try {
			return (AnimationTexture) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
