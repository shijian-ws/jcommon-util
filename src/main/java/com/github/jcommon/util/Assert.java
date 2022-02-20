package com.github.jcommon.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * 断言工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public final class Assert {
    /**
     * 断言集合为null或空集合, 不满足抛出异常
     */
    public static <T extends Collection> T empty(T coll, String errorMsg, Object... args) {
        if (coll != null && !coll.isEmpty()) {
            throwException(errorMsg, args);
        }
        return coll;
    }

    /**
     * 断言集合不为null或不为空集合, 不满足抛出异常
     */
    public static <T extends Collection> T notEmpty(T coll, String errorMsg, Object... args) {
        if (coll == null || coll.isEmpty()) {
            throwException(errorMsg, args);
        }
        return coll;
    }

    /**
     * 断言Map为null或空Map, 不满足抛出异常
     */
    public static <T extends Map> T empty(T map, String errorMsg, Object... args) {
        if (map != null && !map.isEmpty()) {
            throwException(errorMsg, args);
        }
        return map;
    }

    /**
     * 断言Map不为null且非空Map, 不满足抛出异常
     */
    public static <T extends Map> T notEmpty(T map, String errorMsg, Object... args) {
        if (map == null || map.isEmpty()) {
            throwException(errorMsg, args);
        }
        return map;
    }

    /**
     * 断言字符串为null或空串, 不满足抛出异常
     */
    public static <T extends CharSequence> T empty(T cs, String errorMsg, Object... args) {
        if (cs != null && cs.length() > 0) {
            throwException(errorMsg, args);
        }
        return cs;
    }

    /**
     * 断言字符串不为null且不为空串, 不满足抛出异常
     */
    public static <T extends CharSequence> T notEmpty(T cs, String errorMsg, Object... args) {
        if (cs == null || cs.length() == 0) {
            throwException(errorMsg, args);
        }
        return cs;
    }

    /**
     * 断言字符串为null或空串或全部空白字符, 不满足抛出异常
     */
    public static <T extends CharSequence> T notBlank(T cs, String errorMsg, Object... args) {
        if (StringUtil.isBlank(cs)) {
            throwException(errorMsg, args);
        }
        return cs;
    }

    /**
     * 断言一个对象不为null, 不满足抛出异常
     */
    public static <T> T notNull(T obj, String errorMsg, Object... args) {
        if (obj == null) {
            throwException(errorMsg, args);
        }
        return obj;
    }

    /**
     * 断言是否满足条件, 不满足抛出异常
     */
    public static void isTrue(boolean exp, String errorMsg, Object... args) {
        if (!exp) {
            throwException(errorMsg, args);
        }
    }

    /**
     * 断言是否满足条件, 不满足抛出异常
     */
    public static void isTrue(BooleanSupplier supplier, String errorMsg, Object... args) {
        if (!supplier.getAsBoolean()) {
            throwException(errorMsg, args);
        }
    }

    private static void throwException(String errorMsg, Object... args) {
        throw new IllegalArgumentException(StringUtil.formart(errorMsg, args));
    }

    private Assert() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
