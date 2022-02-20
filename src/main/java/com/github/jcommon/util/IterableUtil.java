package com.github.jcommon.util;

import com.github.jcommon.iterator.OnceIterator;
import com.github.jcommon.iterator.TransformedIterator;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 迭代工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public final class IterableUtil {
    /**
     * 迭代器合并类
     */
    private static class ConcatenatedIterable<T> implements Iterable<T> {
        private final Iterable<T>[] iterables;

        private ConcatenatedIterable(Iterable<T>[] iterables) {
            this.iterables = iterables;
        }

        @Override
        public Iterator<T> iterator() {
            return concat((Iterator[]) Stream.of(iterables).map(Iterable::iterator).toArray(Iterator[]::new));
        }
    }

    /**
     * 多个可迭代对象合并为一个可迭代对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> concat(Iterable<?>... array) {
        if (Safes.isEmpty(array)) {
            return Collections.emptySet();
        }

        // 可能存在重复的可迭代对象
        Iterable<T>[] iterables = Stream.of(array).filter(Objects::nonNull).distinct().toArray(Iterable[]::new);
        if (Safes.isEmpty(iterables)) {
            return Collections.emptySet();
        }

        return new ConcatenatedIterable<>(iterables);
    }

    /**
     * 迭代器合并类
     */
    private static class ConcatenatedIterator<T> implements Iterator<T> {
        /**
         * 当前被迭代的迭代器
         */
        private Iterator<? extends T> iterator;
        /**
         * 当前迭代器集
         */
        private Iterator<Iterator<? extends T>> topMetaIterator;
        /**
         * 保存其他迭代器集的队列, 因为可能存在着ConcatenatedIterator本身被再次被合并, 所以当iterator为ConcatenatedIterator类型时,
         * topMetaIterator被重新放入metaIterators的头部, ConcatenatedIterator.metaIterators被全部放入当前的metaIterators中
         * topMetaIterator将被指向ConcatenatedIterator.topMetaIterator, iterator将被指向ConcatenatedIterator.iterator
         */
        private Deque<Iterator<Iterator<? extends T>>> metaIterators;

        private ConcatenatedIterator(Iterator<? extends T>[] array) {
            if (Safes.isNotEmpty(array)) {
                topMetaIterator = Stream.of(array).filter(Objects::nonNull).iterator();
            }
        }

        /**
         * 获取当前迭代器集
         */
        private Iterator<Iterator<? extends T>> getTopMetaIterator() {
            while (Safes.isEmpty(topMetaIterator)) {
                if (Safes.isEmpty(metaIterators)) {
                    return null;
                }
                topMetaIterator = metaIterators.pollFirst();
            }
            return topMetaIterator;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean hasNext() {
            while (Safes.isEmpty(iterator)) {
                // 当前迭代器没有下一个元素, 获取迭代器集
                topMetaIterator = getTopMetaIterator();
                if (Safes.isEmpty(topMetaIterator)) {
                    // 没有元素
                    return false;
                }

                // 取出当前要迭代的迭代器
                iterator = topMetaIterator.next();

                if (iterator instanceof ConcatenatedIterator) {
                    // 当前迭代器本身就是合并对象
                    ConcatenatedIterator<T> concat = (ConcatenatedIterator<T>) iterator;
                    if (metaIterators == null) {
                        metaIterators = new ArrayDeque<>();
                    }

                    // 将topMetaIterator放入当前metaIterators的头部
                    metaIterators.addFirst(topMetaIterator);

                    Deque<Iterator<Iterator<? extends T>>> concatMetaIterators = concat.metaIterators;
                    if (concatMetaIterators != null) {
                        // concat存在其他需要被迭代集合, 则优先于topMetaIterator
                        while (Safes.isNotEmpty(concatMetaIterators)) {
                            metaIterators.addFirst(concatMetaIterators.pollLast());
                        }
                    }

                    topMetaIterator = concat.topMetaIterator;
                    iterator = concat.iterator;
                }
            }

            return iterator.hasNext();
        }

        @Override
        public T next() {
            if (this.hasNext()) {
                return iterator.next();
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * 多个迭代器合并为一个迭代器
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> concat(Iterator<?>... array) {
        if (Safes.isEmpty(array)) {
            return Collections.emptyIterator();
        }

        // 可能存在重复的迭代器
        Iterator<T>[] iterators = Stream.of(array).filter(Objects::nonNull).distinct().toArray(Iterator[]::new);
        if (Safes.isEmpty(iterators)) {
            return Collections.emptyIterator();
        }

        return new ConcatenatedIterator<>(iterators);
    }

    public static <T> Iterator<T> of(T obj) {
        return OnceIterator.of(obj);
    }

    public static <T> Iterator<T> of(Supplier<T> func) {
        return OnceIterator.of(func);
    }

    public static <T> Iterator<T> of(Enumeration<T> enumeration) {
        if (enumeration == null) {
            return Collections.emptyIterator();
        }
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }

    public static <T> Iterable<T> asIterable(T obj) {
        return () -> OnceIterator.of(obj);
    }

    public static <T> Iterable<T> asIterable(Supplier<T> func) {
        return () -> OnceIterator.of(func);
    }

    public static <F, T> Iterator<T> transform(Iterator<F> iterator, Function<F, T> func) {
        return TransformedIterator.of(iterator, func);
    }

    public static <F, T> Iterable<T> transform(Iterable<F> iterable, Function<F, T> func) {
        if (iterable == null) {
            return Collections.emptySet();
        }
        Assert.notNull(func, "func must be not null");
        return () -> TransformedIterator.of(iterable.iterator(), func);
    }

    public static <T> Iterable<T> empty() {
        return Collections.emptyList();
    }

    private IterableUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
