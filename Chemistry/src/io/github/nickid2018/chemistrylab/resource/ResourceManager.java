package io.github.nickid2018.chemistrylab.resource;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Function;

/**
 * A simple wrapper around resource loading should anyone decide to change their
 * minds how this is meant to work in the future.
 *
 * @author Nickid2018
 */
public class ResourceManager {

    public static final Logger logger = LogManager.getLogger("Resource Manager");

    /**
     * The list of locations to be searched
     */
    private static final ArrayList<ResourceLocation> locations = new ArrayList<>();
    private static final ArrayList<String> respacks = new ArrayList<>();
    private static boolean canFuzzy = false;

    /**
     * Add a location that will be searched for resources
     *
     * @param location The location that will be searched for resources
     */
    public static void addResourceLocation(ResourceLocation location) {
        locations.add(location);
    }

    /**
     * Remove a location that will be no longer be searched for resources
     *
     * @param location The location that will be removed from the search list
     */
    public static void removeResourceLocation(ResourceLocation location) {
        locations.remove(location);
    }

    /**
     * Remove all the locations, no resources will be found until new locations have
     * been added
     */
    public static void removeAllResourceLocations() {
        locations.clear();
        respacks.clear();
    }

    /**
     * Get a resource
     *
     * @param ref The reference to the resource to retrieve
     * @return A stream from which the resource can be read
     */
    public static InputStream getResourceAsStream(String ref) {
        return getResourceAsStream(ref, false);
    }

    /**
     * Get a resource
     *
     * @param ref The reference to the resource to retrieve
     * @param seq If true, sequence the stream
     * @return A stream from which the resource can be read
     */
    public static InputStream getResourceAsStream(String ref, boolean seq) {
        boolean savedFuzzy = getCanFuzzy();
        setCanFuzzy(seq);
        boolean find = false;
        Vector<InputStream> lst = new Vector<>();
        for (ResourceLocation location : locations) {
            InputStream in = location.getResourceAsStream(ref);
            if (in != null) {
                if (!seq) {
                    return in;
                } else {
                    find = true;
                    lst.add(in);
                }
            }
        }
        if (!find) {
            setCanFuzzy(savedFuzzy);
            throw new RuntimeException("Resource not found: " + ref);
        }
        setCanFuzzy(savedFuzzy);
        SequenceInputStream sis = new SequenceInputStream(lst.elements());
        return new BufferedInputStream(sis);
    }

    /**
     * Check if a resource is available from any given resource loader
     *
     * @param ref A reference to the resource that should be checked
     * @return True if the resource can be located
     */
    public static boolean resourceExists(String ref) {
        for (ResourceLocation location : locations) {
            URL url = location.getResource(ref);
            if (url != null)
                return true;
        }
        return false;
    }

    /**
     * Get a resource as a URL
     *
     * @param ref The reference to the resource to retrieve
     * @return A URL from which the resource can be read
     */
    public static URL getResource(String ref) {
        return (URL) getFile(ref, (location) -> location.getResource(ref));
    }

    public static OutputStream getOutputStream(String ref) {
        return (OutputStream) getFile(ref, (location) -> location.getOutputStream(ref));
    }

    public static Object getFile(String ref, Function<ResourceLocation, Object> function) {
        for (ResourceLocation location : locations) {
            Object obj = function.apply(location);
            if (obj != null) {
                return obj;
            }
        }
        throw new RuntimeException("Resource not found: " + ref);
    }

    public static boolean getCanFuzzy() {
        return canFuzzy;
    }

    public static void setCanFuzzy(boolean fuzzy) {
        canFuzzy = fuzzy;
    }

    public static ArrayList<String> getResourcePacks() {
        return respacks;
    }

    public static void flushStream() throws IOException {
        for (ResourceLocation location : locations) {
            if (location instanceof ZipFileLocation zip)
                zip.flushFile();
        }
    }

    public static void loadPacks() {
        locations.add(new ClasspathLocation());
        locations.add(new AbsoluteLocation());
        locations.add(new FileSystemLocation(new File(".")));
        File resources = new File("resources");
        if (resources.isDirectory()) {
            File[] files = resources.listFiles((FilenameFilter) new SuffixFileFilter("zip", IOCase.SYSTEM));
            for (File f : Objects.requireNonNull(files)) {
                String namespace = f.getName().split("\\.")[0];
                try {
                    locations.add(new ZipFileLocation(namespace));
                    respacks.add(namespace);
                } catch (IOException e) {
                    ResourceManager.logger.warn("Failed to load resource package " + namespace + ":" + e.getMessage());
                }
            }
        }
    }

    public static void reloadPacks() {
        removeAllResourceLocations();
        loadPacks();
    }
}
