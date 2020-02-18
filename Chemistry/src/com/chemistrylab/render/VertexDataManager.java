package com.chemistrylab.render;

import java.nio.*;
import java.util.*;
import org.lwjgl.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public final class VertexDataManager {

	public static final VertexDataManager MANAGER = new VertexDataManager();

	public static final byte[] indices = { 0, 1, 2, 2, 3, 0 };
	public static final int indicesCount = indices.length;
	public static final ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);

	private static final Set<VBOData> can_reload = new HashSet<>();

	static {
		indicesBuffer.put(indices);
		indicesBuffer.flip();
	}

	private Map<Integer, Integer> vao_indexes = new HashMap<>();
	private Map<Integer, Set<Integer>> saved_eles = new HashMap<>();
	private Map<Integer, Set<Integer>> saved_vaos = new HashMap<>();

	private VertexDataManager() {
	}

	public void addReloadableVBOData(VBOData data) {
		can_reload.add(data);
	}

	public void removeReloadableVBOData(VBOData data) {
		can_reload.remove(data);
	}

	public int newVertexArrays() {
		int vao = glGenVertexArrays();
		saved_vaos.put(vao, new HashSet<>());
		saved_eles.put(vao, new HashSet<>());
		vao_indexes.put(vao, 0);
		return vao;
	}

	public int newVertexBuffer(int vao) {
		int vbo = glGenBuffers();
		saved_vaos.get(vao).add(vbo);
		vao_indexes.replace(vao, vao_indexes.get(vao) + 1);
		return vbo;
	}

	public int newElementArrayBuffer(int vao) {
		int ele = glGenBuffers();
		saved_eles.get(vao).add(ele);
		vao_indexes.replace(vao, vao_indexes.get(vao) + 1);
		return ele;
	}

	public void releaseResource() {
		for (Map.Entry<Integer, Set<Integer>> en : saved_vaos.entrySet()) {
			glBindVertexArray(en.getKey());
			for (int i = 0; i < vao_indexes.get(en.getKey()); i++) {
				glDisableVertexAttribArray(i);
			}
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			for (Integer vbo : en.getValue()) {
				glDeleteBuffers(vbo);
			}
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			for (Integer ele : saved_eles.get(en.getKey())) {
				glDeleteBuffers(ele);
			}
			glBindVertexArray(0);
			glDeleteVertexArrays(en.getKey());
		}
		vao_indexes.clear();
		saved_eles.clear();
		saved_vaos.clear();
		FastQuad.shader_not_load = true;
		FastTexture.shader_not_load = true;
	}

	public void reload() {
		can_reload.forEach(VBOData::reload);
	}
}
