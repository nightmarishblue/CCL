package org.example.helper;

import java.util.Iterator;

// another baffling miss from the great minds of Java
public class Iterate {
    public static <T> Iterable<T> on(Iterator<T> iterator) {
        return () -> iterator;
    }
}
