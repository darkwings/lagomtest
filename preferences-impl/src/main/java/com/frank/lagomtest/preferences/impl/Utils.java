package com.frank.lagomtest.preferences.impl;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author ftorriani
 */
public final class Utils {


    public static <T> Stream<T> iteratorToStream( final Iterator<T> iterator, final boolean parallell) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallell);
    }
}
