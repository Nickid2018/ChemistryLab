package com.chemistrylab.render;

import java.nio.*;
import org.lwjgl.*;
import org.newdawn.slick.opengl.*;
import com.chemistrylab.eventbus.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static com.chemistrylab.ChemistryLab.*;

public class FastTexture {

	public static final byte[] indices = { 0, 1, 2, 2, 3, 0 };
	public static final int indicesCount = indices.length;
	public static final ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
	public static int texture_pid;
	public static int texture_vsid;
	public static int texture_fsid;
	static {
		try {
			indicesBuffer.put(indices);
			indicesBuffer.flip();
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
	private Texture tex;
	private int vaoId;
	private int vboId;
	private int vboiId;

	public FastTexture(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1, Texture t) {
		// OpenGL (0,0)
		float x_center = nowWidth / 2;
		float y_center = nowHeight / 2;
		// To normal person
		float tmp = y0;
		y0 = y1;
		y1 = tmp;
		float o_x0 = x0 / x_center - 1;
		float o_x1 = x1 / x_center - 1;
		float o_y0 = y0 / y_center - 1;
		float o_y1 = y1 / y_center - 1;
		v0.setXYZ(o_x0, o_y0, 0);
		v0.setRGB(1, 0, 0);
		v0.setST(s0, t0);
		v1.setXYZ(o_x0, o_y1, 0);
		v1.setRGB(0, 1, 0);
		v1.setST(s0, t1);
		v2.setXYZ(o_x1, o_y1, 0);
		v2.setRGB(0, 0, 1);
		v2.setST(s1, t1);
		v3.setXYZ(o_x1, o_y0, 0);
		v3.setRGB(1, 1, 1);
		v3.setST(s1, t0);
		tex = t;
		TextureVertex[] vertices = new TextureVertex[] { v0, v1, v2, v3 };
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * TextureVertex.elementCount);
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
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

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
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public Texture getTexture() {
		return tex;
	}

	public final void render() {
		glUseProgram(texture_pid);
		tex.bind();
		glBindVertexArray(vaoId);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glBindVertexArray(0);
		glUseProgram(0);
		glDisable(GL_TEXTURE_2D);
	}
}
