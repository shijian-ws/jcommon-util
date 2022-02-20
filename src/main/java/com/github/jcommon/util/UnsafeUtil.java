package com.github.jcommon.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Unsafe工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-01-23
 */
public final class UnsafeUtil {
    private static final Unsafe UNSAFE;

    static {
        Object unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = field.get(null);
        } catch (Exception e) {
        }
        UNSAFE = (Unsafe) unsafe;
    }

    private UnsafeUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
