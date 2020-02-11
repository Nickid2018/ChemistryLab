package com.chemistrylab.render;

public class TextureVertex {
	// Vertex data
	private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
	private float[] st = new float[] { 0f, 0f };

	// The amount of bytes an element has
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int textureElementCount = 2;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int textureByteCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int textureByteOffset = colorByteOffset + colorByteCount;

	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionBytesCount + colorByteCount + textureByteCount;

	// Setters
	public TextureVertex setXYZ(float x, float y, float z) {
		return setXYZW(x, y, z, 1f);
	}

	public TextureVertex setRGB(float r, float g, float b) {
		return setRGBA(r, g, b, 1f);
	}

	public TextureVertex setST(float s, float t) {
		st = new float[] { s, t };
		return this;
	}

	public TextureVertex setXYZW(float x, float y, float z, float w) {
		xyzw = new float[] { x, y, z, w };
		return this;
	}

	public TextureVertex setRGBA(float r, float g, float b, float a) {
		rgba = new float[] { r, g, b, 1f };
		return this;
	}

	// Getters
	public float[] getElements() {
		float[] out = new float[TextureVertex.elementCount];
		int i = 0;

		// Insert XYZW elements
		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		// Insert RGBA elements
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		// Insert ST elements
		out[i++] = this.st[0];
		out[i++] = this.st[1];

		return out;
	}

	public float[] getXYZW() {
		return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3] };
	}

	public float[] getRGBA() {
		return new float[] { this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3] };
	}

	public float[] getST() {
		return new float[] { this.st[0], this.st[1] };
	}
}