
package com.example.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class IDGenerator {

    private static final ConcurrentHashMap<Class<?>, AtomicLong> mapper = new ConcurrentHashMap<>();

    private IDGenerator() {
    }

    public static long generateId(Class<?> clazz) {

        mapper.putIfAbsent(clazz, new AtomicLong(1)); // NOSONAR
        return mapper.get(clazz).getAndIncrement();
    }
}
