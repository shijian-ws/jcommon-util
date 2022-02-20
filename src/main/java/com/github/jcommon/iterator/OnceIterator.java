package com.github.jcommon.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 可迭代一次的迭代器
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public class OnceIterator<T> implements Iterator<T> {
    private Supplier<T> func;

    private OnceIterator(Supplier<T> func) {
        this.func = func;
    }

    @Override
    public boolean hasNext() {
        return func != null;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            T result = this.func.get();
            this.func = null;
            return result;
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(func);
    }

    @Override
    public String toString() {
        return "OnceIterator{" +
                "func=" + func +
                ", hasNext=" + this.hasNext() +
                '}';
    }

    public static <T> Iterator<T> of(T obj) {
        return new OnceIterator<>(obj == null ? null : () -> obj);
    }

    public static <T> Iterator<T> of(Supplier<T> func) {
        return new OnceIterator<>(func);
    }
}
