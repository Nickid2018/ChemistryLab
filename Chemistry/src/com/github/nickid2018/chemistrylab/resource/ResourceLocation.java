package com.github.nickid2018.chemistrylab.resource;

import java.io.*;
import java.net.*;

/**
 * A location from which resources can be loaded
 * 
 * @author Nickid2018
 */
public interface ResourceLocation {

	/**
	 * Get a resource as an input stream
	 * 
	 * @param ref The reference to the resource to retrieve
	 * @return A stream from which the resource can be read or null if the resource
	 *         can't be found in this location
	 */
	public InputStream getResourceAsStream(String ref);

	/**
	 * Get a resource as a URL
	 * 
	 * @param ref The reference to the resource to retrieve
	 * @return A URL from which the resource can be read
	 */
	public URL getResource(String ref);

	/**
	 * Get a resource as an output stream
	 * 
	 * @param ref The reference to the resource to retrieve
	 * @return A OutputStream from which the resource can be written
	 */
	public OutputStream getOutputStream(String ref);
}
