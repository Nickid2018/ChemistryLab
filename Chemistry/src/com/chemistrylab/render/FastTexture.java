package com.chemistrylab.render;

import java.nio.*;
import java.util.Objects;

import org.lwjgl.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static com.chemistrylab.ChemistryLab.*;

public class FastTexture {

	public static final int POSTION_LEFT_UP = 0;
	public static final int POSTION_LEFT_DOWN = 1;
	public static final int POSTION_RIGHT_DOWN = 2;
	public static final int POSTION_RIGHT_UP = 3;

	public static int texture_pid;
	public static int texture_vsid;
	public static int texture_fsid;
	static {
		try {
			texture_pid = ShaderManager.MANAGER.createProgram();
			texture_vsid = ShaderManager.MANAGER.attachVertexShader(texture_pid, "assets/shader/simple_texture.vsh");
			texture_fsid = ShaderManager.MANAGER.attachFragmentShader(texture_pid, "assets/shader/simple_texture.fsh");
			// Position information will be attribute 0
			glBindAttribLocation(texture_pid, 0, "in_Position");
			// Color information will be attribute 1
			glBindAttribLocation(texture_pid, 1, "in_Color");
			// Textute information will be attribute 2
			glBindAttribLocation(texture_pid, 2, "in_TextureCoord");
			glLinkProgram(texture_pid);
			glValidateProgram(texture_pid);
		} catch (Exception e) {
			Event ev = THREAD_FATAL.clone();
			ev.putExtra(0, e);
			ev.putExtra(1, Thread.currentThread());
			EventBus.postEvent(ev);
		}
	}

	private TextureVertex v0 = new TextureVertex();
	private TextureVertex v1 = new TextureVertex();
	private TextureVertex v2 = new TextureVertex();
	private TextureVertex v3 = new TextureVertex();
	private FloatBuffer buffer;
	private Texture tex;
	private int vaoId;
	private int vboId;
	private int vboiId;
	private boolean stream;

	public FastTexture(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1, Texture t) {
		this(x0, y0, x1, y1, s0, t0, s1, t1, t, false);
	}

	public FastTexture(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1, Texture t,
			boolean stream) {

		Objects.requireNonNull(t, "texture");

		this.stream = stream;

		if (stream) {
			buffer = BufferUtils.createFloatBuffer(TextureVertex.elementCount);
		}

		float o_x0 = CommonRender.toGLX(x0);
		float o_x1 = CommonRender.toGLX(x1);
		float o_y0 = CommonRender.toGLY(y0);
		float o_y1 = CommonRender.toGLY(y1);

		v0.setXYZ(o_x0, o_y0, 0).setRGB(0, 0, 0).setST(s0, t0);
		v1.setXYZ(o_x0, o_y1, 0).setRGB(0, 0, 0).setST(s0, t1);
		v2.setXYZ(o_x1, o_y1, 0).setRGB(0, 0, 0).setST(s1, t1);
		v3.setXYZ(o_x1, o_y0, 0).setRGB(0, 0, 0).setST(s1, t0);

		tex = t;

		TextureVertex[] vertices = new TextureVertex[] { v0, v1, v2, v3 };
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
		glVertexAttribPointer(0, TextureVertex.positionElementCount, GL_FLOAT, false, TextureVertex.stride,
				TextureVertex.positionByteOffset);
		// Put the color components in attribute list 1
		glVertexAttribPointer(1, TextureVertex.colorElementCount, GL_FLOAT, false, TextureVertex.stride,
				TextureVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		glVertexAttribPointer(2, TextureVertex.textureElementCount, GL_FLOAT, false, TextureVertex.stride,
				TextureVertex.textureByteOffset);

		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		glBindVertexArray(0);

		vboiId = VertexDataManager.MANAGER.newElementArrayBuffer(vaoId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, VertexDataManager.indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public Texture getTexture() {
		return tex;
	}

	public final TextureVertex getVertex(int pos) {
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

	public final void updateVertex(int pos, TextureVertex v) {
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
		glBufferSubData(GL_ARRAY_BUFFER, pos * TextureVertex.stride, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public final void render() {
		glUseProgram(texture_pid);
		tex.bind();
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glDrawElements(GL_TRIANGLES, VertexDataManager.indicesCount, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		glUseProgram(0);
		glDisable(GL_TEXTURE_2D);
	}
}