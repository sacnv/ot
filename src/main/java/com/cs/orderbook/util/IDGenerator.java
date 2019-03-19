
package com.cs.orderbook.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class IDGenerator {

    private static final ConcurrentHashMap<Class<?>, AtomicLong> MAPPER =
            new ConcurrentHashMap<>();

    private IDGenerator() {
    }

    public static long generateId(Class<?> clazz) {
        MAPPER.putIfAbsent(clazz, new AtomicLong(1)); // NOSONAR
        return MAPPER.get(clazz).getAndIncrement();
    }
}
