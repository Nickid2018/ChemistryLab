package io.github.nickid2018.chemistrylab.resource;

import java.io.*;
import java.net.URL;

public class AbsoluteLocation implements ResourceLocation {

    @Override
    public InputStream getResourceAsStream(String ref) {
        try {
            return new FileInputStream(ref);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public URL getResource(String ref) {
        try {
            return new File(ref).toURI().toURL();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public OutputStream getOutputStream(String ref) {
        try {
            return new FileOutputStream(ref);
        } catch (Exception e) {
            return null;
        }
    }

}
