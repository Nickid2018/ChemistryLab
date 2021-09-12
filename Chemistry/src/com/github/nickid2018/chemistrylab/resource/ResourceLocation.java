package com.github.nickid2018.chemistrylab.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
     * can't be found in this location
     */
    InputStream getResourceAsStream(String ref);

    /**
     * Get a resource as a URL
     *
     * @param ref The reference to the resource to retrieve
     * @return A URL from which the resource can be read
     */
    URL getResource(String ref);

    /**
     * Get a resource as an output stream
     *
     * @param ref The reference to the resource to retrieve
     * @return A OutputStream from which the resource can be written
     */
    OutputStream getOutputStream(String ref);
}
