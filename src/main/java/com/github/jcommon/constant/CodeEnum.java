package com.github.jcommon.constant;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 枚举基类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public interface CodeEnum<V, E extends Enum<E>> extends Comparable<E> {
    /**
     * 获取Code
     */
    V getCode();

    /**
     * 获取描述
     */
    default String getDesc() {
        return CommonConstant.STRING_EMPTY;
    }

    /**
     * 根据code获取枚举
     */
    static <V, E extends Enum<E>, T extends CodeEnum<V, E>> T getByCode(Class<T> enumClass, V code) {
        return enumClass == null || code == null ? null : Stream.of(enumClass.getEnumConstants()).filter(e -> Objects.equals(e.getCode(), code)).findAny().orElse(null);
    }
}
