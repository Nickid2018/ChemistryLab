package com.chemistrylab.render;

import java.io.*;
import java.util.*;
import java.nio.charset.*;
import com.chemistrylab.util.*;
import org.apache.commons.io.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public final class ShaderManager {

	public static final ShaderManager MANAGER = new ShaderManager();

	private Map<Integer, Set<Integer>> saved_pIds = new HashMap<>();

	private ShaderManager() {
	}

	public int createProgram() {
		int pId = glCreateProgram();
		saved_pIds.put(pId, new HashSet<>());
		return pId;
	}

	public int attachVertexShader(int pId, String ref) throws IOException {
		InputStream is = ResourceManager.getResourceAsStream(ref);
		StringBuilder shaderSource = new StringBuilder(IOUtils.toString(is, Charset.defaultCharset()));
		int shaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new IOException("Invalid Shader");
		}
		saved_pIds.get(pId).add(shaderID);
		glAttachShader(pId, shaderID);
		return shaderID;
	}

	public int attachFragmentShader(int pId, String ref) throws IOException {
		InputStream is = ResourceManager.getResourceAsStream(ref);
		StringBuilder shaderSource = new StringBuilder(IOUtils.toString(is, Charset.defaultCharset()));
		int shaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new IOException("Invalid Shader");
		}
		saved_pIds.get(pId).add(shaderID);
		glAttachShader(pId, shaderID);
		return shaderID;
	}

	public void releaseResource() {
		for (Map.Entry<Integer, Set<Integer>> en : saved_pIds.entrySet()) {
			glUseProgram(0);
			for (Integer s : en.getValue()) {
				glDetachShader(en.getKey(), s);
			}

			for (Integer s : en.getValue()) {
				glDeleteShader(s);
			}
			glDeleteProgram(en.getKey());
		}
	}
}
