package com.example.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class IDGenerator {

    private static final ConcurrentHashMap<Class<?>, AtomicLong> mapper = new ConcurrentHashMap<Class<?>, AtomicLong>();

    private IDGenerator () {}

    public static long generateId (Class<?> _class) {
    	
        mapper.putIfAbsent(_class, new AtomicLong(1));
        return mapper.get(_class).getAndIncrement();
    }
}