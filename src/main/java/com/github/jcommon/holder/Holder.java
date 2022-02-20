package com.github.jcommon.holder;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 持有引用, 可用于分段分为锁
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public class Holder<T> {
    private volatile T value;

    /**
     * 设置持有对象
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * 获取持有对象
     */
    public T get() {
        return this.value;
    }

    /**
     * 移除持有对象
     */
    public void remove() {
        this.value = null;
    }

    /**
     * 持有对象时返回true
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * 未持有对象时返回true
     */
    public boolean isAbsent() {
        return value == null;
    }

    /**
     * 不存在通过函数放入并返回, 多线程安全
     */
    public T computeIfAbsent(Supplier<T> supplier) {
        if (value == null && supplier != null) {
            synchronized (this) {
                if (value == null) {
                    value = supplier.get();
                }
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Holder<?> holder = (Holder<?>) o;
        return Objects.equals(value, holder.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Holder{" + value + '}';
    }
}
