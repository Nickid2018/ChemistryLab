package com.github.nickid2018.chemistrylab.render;

public class Vertex {
	// Vertex data
	private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };

	// The amount of elements that a vertex has
	public static final int elementCount = 8;
	// The amount of bytes an element has
	public static final int elementBytes = 4;
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int sizeInBytes = elementBytes * elementCount;

	// Setters
	public Vertex setXYZ(float x, float y, float z) {
		return this.setXYZW(x, y, z, 1f);
	}

	public Vertex setRGB(float r, float g, float b) {
		return this.setRGBA(r, g, b, 1f);
	}

	public Vertex setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] { x, y, z, w };
		return this;
	}

	public Vertex setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] { r, g, b, a };
		return this;
	}

	// Getters
	public float[] getXYZW() {
		return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3] };
	}

	public float[] getRGBA() {
		return new float[] { this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3] };
	}

	public float[] getElements() {
		float[] out = new float[Vertex.elementCount];
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

		return out;
	}
}