package io.github.nickid2018.chemistrylab.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassUtils {

    private static final URLClassLoader loader = (URLClassLoader) URLClassLoader.getSystemClassLoader();

    public static void addURL(String path) throws Exception {
        URL url = new File(path).toURI().toURL();
        if (!isLoad(url)) {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            add.invoke(loader, url);
        }
    }

    public static boolean isLoad(URL jar) {
        for (URL url : loader.getURLs()) {
            if (url.equals(jar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the class of caller. Hack of Reflection.
     *
     * @return The class of caller
     */
    public static Class<?> getCallerClass() {
        // Warning
        // Reflect Class Operation
        // Destination: sun.reflect.Reflection
        // Function to Reflect: getCallerClass(I)Ljava.lang.Class;
        // Function Warning: Deprecated at defined class
        // Version can work: Java 8 (Also can run in Java 6 and 7)
        try {
            Class<?> reflc = Class.forName("sun.reflect.Reflection");
            // Invoke Function Stack:
            // 3: Invoke Stack Want to know
            // 2: Invoke Stack
            // 1: This Function Stack
            // 0: java.lang.Method.invoke
            // Top: sun.reflect.Reflection.getCallerClass Function Stack (Native
            // Method Stack)
            return (Class<?>) (reflc.getMethod("getCallerClass", int.class)).invoke(reflc, 3);
        } catch (Throwable e) {
            // Actually, this function won't throw any error. (Except
            // OutOfMemoryError or StackOverflowError)
            return null;
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... classes) {
        try {
            return type.getConstructor(classes);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find the constructor!");
        }
    }

    public static <T> Constructor<T> getDeclaredConstructor(Class<T> type, Class<?>... classes) {
        try {
            return type.getDeclaredConstructor(classes);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find the constructor!");
        }
    }
}
