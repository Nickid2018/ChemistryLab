package com.chemistrylab.render;

import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import org.newdawn.slick.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static com.chemistrylab.ChemistryLab.*;

public class FastQuad implements VBOData {

	public static final int POSTION_LEFT_UP = 0;
	public static final int POSTION_LEFT_DOWN = 1;
	public static final int POSTION_RIGHT_DOWN = 2;
	public static final int POSTION_RIGHT_UP = 3;

	public static int quad_pid;
	public static int quad_vsid;
	public static int quad_fsid;
	static {
		try {
			quad_pid = ShaderManager.MANAGER.createProgram();
			quad_vsid = ShaderManager.MANAGER.attachVertexShader(quad_pid, "assets/shader/simple.vsh");
			quad_fsid = ShaderManager.MANAGER.attachFragmentShader(quad_pid, "assets/shader/simple.fsh");
			// Position information will be attribute 0
			glBindAttribLocation(quad_pid, 0, "in_Position");
			// Color information will be attribute 1
			glBindAttribLocation(quad_pid, 1, "in_Color");
			glLinkProgram(quad_pid);
			glValidateProgram(quad_pid);
		} catch (Exception e) {
			Event ev = THREAD_FATAL.clone();
			ev.putExtra(0, e);
			ev.putExtra(1, Thread.currentThread());
			EventBus.postEvent(ev);
		}
	}

	private Vertex v0 = new Vertex();
	private Vertex v1 = new Vertex();
	private Vertex v2 = new Vertex();
	private Vertex v3 = new Vertex();
	private FloatBuffer buffer;
	private int vaoId;
	private int vboId;
	private int vboiId;
	private boolean stream;

	public FastQuad(float x0, float y0, float x1, float y1, Color c) {
		this(x0, y0, x1, y1, c, false);
	}

	public FastQuad(float x0, float y0, float x1, float y1, Color c, boolean stream) {

		Objects.requireNonNull(c, "color");
		VertexDataManager.MANAGER.addReloadableVBOData(this);

		this.stream = stream;

		if (stream) {
			buffer = BufferUtils.createFloatBuffer(Vertex.elementCount);
		}

		float o_x0 = CommonRender.toGLX(x0);
		float o_x1 = CommonRender.toGLX(x1);
		float o_y0 = CommonRender.toGLY(y0);
		float o_y1 = CommonRender.toGLY(y1);
		v0.setXYZ(o_x0, o_y0, 0).setRGBA(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
				c.getAlpha() / 255.0f);
		v1.setXYZ(o_x0, o_y1, 0).setRGBA(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
				c.getAlpha() / 255.0f);
		v2.setXYZ(o_x1, o_y1, 0).setRGBA(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
				c.getAlpha() / 255.0f);
		v3.setXYZ(o_x1, o_y0, 0).setRGBA(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f,
				c.getAlpha() / 255.0f);
		Vertex[] vertices = new Vertex[] { v0, v1, v2, v3 };
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex.sizeInBytes);
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		verticesBuffer.flip();
		vaoId = VertexDataManager.MANAGER.newVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = VertexDataManager.MANAGER.newVertexBuffer(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, stream ? GL_STREAM_DRAW : GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		glVertexAttribPointer(0, 4, GL_FLOAT, false, Vertex.sizeInBytes, 0);
		// Put the color components in attribute list 1
		glVertexAttribPointer(1, 4, GL_FLOAT, false, Vertex.sizeInBytes, Vertex.elementBytes * 4);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);

		vboiId = VertexDataManager.MANAGER.newElementArrayBuffer(vaoId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexDataManager.indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public final Vertex getVertex(int pos) {
		switch (pos) {
		case POSTION_LEFT_UP:
			return v0;
		case POSTION_LEFT_DOWN:
			return v1;
		case POSTION_RIGHT_DOWN:
			return v2;
		case POSTION_RIGHT_UP:
			return v3;
		default:
			throw new IllegalArgumentException("position");
		}
	}

	public final void updateVertex(int pos, Vertex v) {
		if (!stream)
			throw new UnsupportedOperationException("The Quad is not streamed.");
		switch (pos) {
		case POSTION_LEFT_UP:
			v0 = v;
			break;
		case POSTION_LEFT_DOWN:
			v1 = v;
			break;
		case POSTION_RIGHT_DOWN:
			v2 = v;
			break;
		case POSTION_RIGHT_UP:
			v3 = v;
			break;
		default:
			throw new IllegalArgumentException("position");
		}
		buffer.rewind();
		buffer.put(v.getElements());
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferSubData(GL_ARRAY_BUFFER, pos * Vertex.sizeInBytes, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public final void render() {
		glUseProgram(quad_pid);
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glDrawElements(GL_TRIANGLES, VertexDataManager.indicesCount, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		glUseProgram(0);
		glDisable(GL_TEXTURE_2D);
		shader_not_load = false;
	}

	public static boolean shader_not_load = false;

	@Override
	public final void reload() {
		// First:Shader
		if (shader_not_load) {
			shader_not_load = false;
			try {
				quad_pid = ShaderManager.MANAGER.createProgram();
				quad_vsid = ShaderManager.MANAGER.attachVertexShader(quad_pid, "assets/shader/simple.vsh");
				quad_fsid = ShaderManager.MANAGER.attachFragmentShader(quad_pid, "assets/shader/simple.fsh");
				// Position information will be attribute 0
				glBindAttribLocation(quad_pid, 0, "in_Position");
				// Color information will be attribute 1
				glBindAttribLocation(quad_pid, 1, "in_Color");
				glLinkProgram(quad_pid);
				glValidateProgram(quad_pid);
			} catch (Exception e) {
				Event ev = THREAD_FATAL.clone();
				ev.putExtra(0, e);
				ev.putExtra(1, Thread.currentThread());
				EventBus.postEvent(ev);
			}
		}
		// Second:VAO,VBO
		Vertex[] vertices = new Vertex[] { v0, v1, v2, v3 };
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex.sizeInBytes);
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		verticesBuffer.flip();
		vaoId = VertexDataManager.MANAGER.newVertexArrays();
		glBindVertexArray(vaoId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = VertexDataManager.MANAGER.newVertexBuffer(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, stream ? GL_STREAM_DRAW : GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		glVertexAttribPointer(0, 4, GL_FLOAT, false, Vertex.sizeInBytes, 0);
		// Put the color components in attribute list 1
		glVertexAttribPointer(1, 4, GL_FLOAT, false, Vertex.sizeInBytes, Vertex.elementBytes * 4);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);

		vboiId = VertexDataManager.MANAGER.newElementArrayBuffer(vaoId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexDataManager.indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
