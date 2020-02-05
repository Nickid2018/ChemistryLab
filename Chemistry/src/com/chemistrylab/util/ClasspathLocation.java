package com.chemistrylab.util;

import java.io.*;
import java.net.*;

/**
 * A resource location that searches the classpath
 * 
 * @author Nickid2018
 */
public class ClasspathLocation implements ResourceLocation {
	
	public URL getResource(String ref) {
		String cpRef = ref.replace('\\', '/');
		return ClasspathLocation.class.getClassLoader().getResource(cpRef);
	}

	public InputStream getResourceAsStream(String ref) {
		String cpRef = ref.replace('\\', '/');
		return ClasspathLocation.class.getClassLoader().getResourceAsStream(cpRef);	
	}

	/*
	 * Can't get output stream in class path
	 */
	@Override
	public OutputStream getOutputStream(String ref) {
		return null;
	}

}

