package io.github.nickid2018.chemistrylab.resource;

import java.io.*;
import java.net.URL;

/**
 * A resource loading location that searches somewhere on the classpath
 *
 * @author Nickid2018
 */
public record FileSystemLocation(File root) implements ResourceLocation {
    /**
     * Create a new resource location based on the file system
     *
     * @param root The root of the file system to search
     */
    public FileSystemLocation {
    }

    /**
     * @see ResourceLocation#getResource(String)
     */
    @Override
    public URL getResource(String ref) {
        try {
            File file = new File(root, ref);
            if (!file.exists())
                file = new File(ref);
            if (!file.exists())
                return null;
            return file.toURI().toURL();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @see ResourceLocation#getResourceAsStream(String)
     */
    @Override
    public InputStream getResourceAsStream(String ref) {
        try {
            File file = new File(root, ref);
            if (!file.exists())
                file = new File(ref);
            return new FileInputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public OutputStream getOutputStream(String ref) {
        try {
            File file = new File(root, ref);
            if (!file.exists())
                file = new File(ref);
            return new FileOutputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

}
