package com.github.nickid2018.chemistrylab.util.pool;

import com.github.nickid2018.chemistrylab.util.ClassUtils;

import java.lang.reflect.Constructor;

public class ReflectionPool<T extends Poolable> extends Pool<T> {

    private final Constructor<T> constructor;

    public ReflectionPool(Class<T> type) {
        this(type, 16, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, Integer.MAX_VALUE);
    }

    public ReflectionPool(Class<T> type, int initialCapacity, int max) {
        super(initialCapacity, max);
        constructor = findConstructor(type);
        if (constructor == null)
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName());
    }

    private Constructor<T> findConstructor(Class<T> type) {
        try {
            return ClassUtils.getConstructor(type);
        } catch (Exception ex1) {
            try {
                Constructor<T> constructor = ClassUtils.getDeclaredConstructor(type);
                constructor.setAccessible(true);
                return constructor;
            } catch (Exception ex2) {
                return null;
            }
        }
    }

    protected T newObject() {
        try {
            return constructor.newInstance((Object[]) null);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create new instance: " + constructor.getDeclaringClass().getName(),
                    ex);
        }
    }
}
