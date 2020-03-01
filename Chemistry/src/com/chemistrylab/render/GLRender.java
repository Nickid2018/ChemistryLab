package com.chemistrylab.render;

import java.nio.*;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.renderer.*;

public class GLRender implements SGL {
	/** The width of the display */
	private int width;
	/** The height of the display */
	private int height;
	/** The current colour */
	private float[] current = new float[] { 1, 1, 1, 1 };
	/** The global colour scale */
	protected float alphaScale = 1;

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#initDisplay(int, int)
	 */
	@Override
	public void initDisplay(int width, int height) {
		this.width = width;
		this.height = height;

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClearDepth(1);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#enterOrtho(int, int)
	 */
	@Override
	public void enterOrtho(int xsize, int ysize) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glTranslatef((width - xsize) / 2, (height - ysize) / 2, 0);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glBegin(int)
	 */
	@Override
	public void glBegin(int geomType) {
		GL11.glBegin(geomType);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glBindTexture(int, int)
	 */
	@Override
	public void glBindTexture(int target, int id) {
		GL11.glBindTexture(target, id);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glBlendFunc(int, int)
	 */
	@Override
	public void glBlendFunc(int src, int dest) {
		GL11.glBlendFunc(src, dest);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glCallList(int)
	 */
	@Override
	public void glCallList(int id) {
		GL11.glCallList(id);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClear(int)
	 */
	@Override
	public void glClear(int value) {
		GL11.glClear(value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClearColor(float, float, float,
	 *      float)
	 */
	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GL11.glClearColor(red, green, blue, alpha);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClipPlane(int,
	 *      java.nio.DoubleBuffer)
	 */
	@Override
	public void glClipPlane(int plane, DoubleBuffer buffer) {
		GL11.glClipPlane(plane, buffer);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glColor4f(float, float, float,
	 *      float)
	 */
	@Override
	public void glColor4f(float r, float g, float b, float a) {
		a *= alphaScale;

		current[0] = r;
		current[1] = g;
		current[2] = b;
		current[3] = a;

		GL11.glColor4f(r, g, b, a);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glColorMask(boolean, boolean,
	 *      boolean, boolean)
	 */
	@Override
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		GL11.glColorMask(red, green, blue, alpha);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glCopyTexImage2D(int, int, int,
	 *      int, int, int, int, int)
	 */
	@Override
	public void glCopyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height,
			int border) {
		GL11.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDeleteTextures(java.nio.IntBuffer)
	 */
	@Override
	public void glDeleteTextures(IntBuffer buffer) {
		GL11.glDeleteTextures(buffer);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDisable(int)
	 */
	@Override
	public void glDisable(int item) {
		GL11.glDisable(item);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glEnable(int)
	 */
	@Override
	public void glEnable(int item) {
		GL11.glEnable(item);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glEnd()
	 */
	@Override
	public void glEnd() {
		GL11.glEnd();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glEndList()
	 */
	@Override
	public void glEndList() {
		GL11.glEndList();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGenLists(int)
	 */
	@Override
	public int glGenLists(int count) {
		return GL11.glGenLists(count);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetFloat(int,
	 *      java.nio.FloatBuffer)
	 */
	@Override
	public void glGetFloat(int id, FloatBuffer ret) {
		ret.put(GL11.glGetFloat(id));
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetInteger(int,
	 *      java.nio.IntBuffer)
	 */
	@Override
	public void glGetInteger(int id, IntBuffer ret) {
		ret.put(GL11.glGetInteger(id));
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetTexImage(int, int, int, int,
	 *      java.nio.ByteBuffer)
	 */
	@Override
	public void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) {
		GL11.glGetTexImage(target, level, format, type, pixels);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glLineWidth(float)
	 */
	@Override
	public void glLineWidth(float width) {
		GL11.glLineWidth(width);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glLoadIdentity()
	 */
	@Override
	public void glLoadIdentity() {
		GL11.glLoadIdentity();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glNewList(int, int)
	 */
	@Override
	public void glNewList(int id, int option) {
		GL11.glNewList(id, option);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glPointSize(float)
	 */
	@Override
	public void glPointSize(float size) {
		GL11.glPointSize(size);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glPopMatrix()
	 */
	@Override
	public void glPopMatrix() {
		GL11.glPopMatrix();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glPushMatrix()
	 */
	@Override
	public void glPushMatrix() {
		GL11.glPushMatrix();
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glReadPixels(int, int, int, int,
	 *      int, int, java.nio.ByteBuffer)
	 */
	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		GL11.glReadPixels(x, y, width, height, format, type, pixels);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glRotatef(float, float, float,
	 *      float)
	 */
	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glScalef(float, float, float)
	 */
	@Override
	public void glScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glScissor(int, int, int, int)
	 */
	@Override
	public void glScissor(int x, int y, int width, int height) {
		GL11.glScissor(x, y, width, height);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexCoord2f(float, float)
	 */
	@Override
	public void glTexCoord2f(float u, float v) {
		GL11.glTexCoord2f(u, v);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexEnvi(int, int, int)
	 */
	@Override
	public void glTexEnvi(int target, int mode, int value) {
		GL11.glTexEnvi(target, mode, value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTranslatef(float, float, float)
	 */
	@Override
	public void glTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glVertex2f(float, float)
	 */
	@Override
	public void glVertex2f(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glVertex3f(float, float, float)
	 */
	@Override
	public void glVertex3f(float x, float y, float z) {
		GL11.glVertex3f(x, y, z);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#flush()
	 */
	@Override
	public void flush() {
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexParameteri(int, int, int)
	 */
	@Override
	public void glTexParameteri(int target, int param, int value) {
		GL11.glTexParameteri(target, param, value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#getCurrentColor()
	 */
	@Override
	public float[] getCurrentColor() {
		return current;
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDeleteLists(int, int)
	 */
	@Override
	public void glDeleteLists(int list, int count) {
		GL11.glDeleteLists(list, count);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glClearDepth(float)
	 */
	@Override
	public void glClearDepth(float value) {
		GL11.glClearDepth(value);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDepthFunc(int)
	 */
	@Override
	public void glDepthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glDepthMask(boolean)
	 */
	@Override
	public void glDepthMask(boolean mask) {
		GL11.glDepthMask(mask);
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#setGlobalAlphaScale(float)
	 */
	@Override
	public void setGlobalAlphaScale(float alphaScale) {
		this.alphaScale = alphaScale;
	}

	/**
	 * @see org.newdawn.slick.opengl.renderer.SGL#glLoadMatrix(java.nio.FloatBuffer)
	 */
	@Override
	public void glLoadMatrix(FloatBuffer buffer) {
		GL11.glLoadMatrixf(buffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGenTextures(java.nio.IntBuffer)
	 */
	@Override
	public void glGenTextures(IntBuffer ids) {
		GL11.glGenTextures(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#glGetError()
	 */
	@Override
	public void glGetError() {
		GL11.glGetError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#glTexImage2D(int, int, int, int,
	 * int, int, int, int, java.nio.ByteBuffer)
	 */
	@Override
	public void glTexImage2D(int target, int i, int dstPixelFormat, int width, int height, int j, int srcPixelFormat,
			int glUnsignedByte, ByteBuffer textureBuffer) {
		GL11.glTexImage2D(target, i, dstPixelFormat, width, height, j, srcPixelFormat, glUnsignedByte, textureBuffer);
	}

	@Override
	public void glTexSubImage2D(int glTexture2d, int i, int pageX, int pageY, int width, int height, int glBgra,
			int glUnsignedByte, ByteBuffer scratchByteBuffer) {
		GL11.glTexSubImage2D(glTexture2d, i, pageX, pageY, width, height, glBgra, glUnsignedByte, scratchByteBuffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#canTextureMirrorClamp()
	 */
	@Override
	public boolean canTextureMirrorClamp() {
		return GL.getCapabilities().GL_EXT_texture_mirror_clamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#canSecondaryColor()
	 */
	@Override
	public boolean canSecondaryColor() {
		return GL.getCapabilities().GL_EXT_secondary_color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.newdawn.slick.opengl.renderer.SGL#glSecondaryColor3ubEXT(byte, byte,
	 * byte)
	 */
	@Override
	public void glSecondaryColor3ubEXT(byte b, byte c, byte d) {
		EXTSecondaryColor.glSecondaryColor3ubEXT(b, c, d);
	}
}
