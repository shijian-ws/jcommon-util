package com.github.jcommon.iterator;

import com.github.jcommon.util.Safes;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * 字符串迭代器
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public class StringIterator implements Iterator<String> {
    private final Iterator<String> iterator;

    private StringIterator(String... array) {
        this.iterator = Safes.isEmpty(array) ? Collections.emptyIterator() : Stream.of(array).iterator();
    }

    private StringIterator(Stream<String> stream) {
        this.iterator = stream == null ? Collections.emptyIterator() : stream.iterator();
    }

    @Override
    public boolean hasNext() {
        return Safes.isNotEmpty(iterator);
    }

    @Override
    public String next() {
        if (hasNext()) {
            return iterator.next();
        }
        throw new NoSuchElementException();
    }

    public static Iterator<String> of(String... array) {
        return new StringIterator(array);
    }

    public static Iterable<String> asIterable(String... array) {
        return () -> new StringIterator(array);
    }
}
