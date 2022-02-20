package com.github.jcommon.util;

import java.util.Objects;

/**
 * 可比较工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-02-06
 */
public final class CompareUtil {
    /**
     * 对象比较, 基于equals, null, Comparable, hashCode
     */
    public static int compare(Object o1, Object o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1 == null) {
            return -1;
        }
        if (o1 instanceof Comparable && o2 instanceof Comparable) {
            try {
                return ((Comparable) o1).compareTo(o2);
            } catch (Throwable e) {
            }
        }
        // TODO 未考虑容器
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }

    private CompareUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
