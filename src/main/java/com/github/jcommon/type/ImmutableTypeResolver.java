package com.github.jcommon.type;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 不可变的类型引用
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-24
 */
public class ImmutableTypeResolver implements TypeResolver {
    private static final Type[] EMPTY_TYPE_ARRAY = {};

    private final Type rawType;
    private final Type[] elementTypes;
    private final Class<?> containerType;

    private ImmutableTypeResolver(Type rawType, Type[] elementTypes, Class<?> containerType) {
        this.rawType = rawType;
        this.elementTypes = elementTypes == null ? EMPTY_TYPE_ARRAY : elementTypes;
        this.containerType = containerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type[] getElementTypes() {
        return elementTypes;
    }

    @Override
    public Class<?> getContainerType() {
        return containerType;
    }

    @Override
    public String toString() {
        return "ImmutableTypeResolver{" +
                "rawType=" + rawType +
                ", elementTypes=" + Arrays.toString(elementTypes) +
                ", containerType=" + containerType +
                '}';
    }

    public static ImmutableTypeResolver of(Type rawType, Type elementType, Class<?> containerType) {
        return new ImmutableTypeResolver(rawType, elementType == null ? null : new Type[]{elementType}, containerType);
    }

    public static ImmutableTypeResolver of(Type rawType, Type[] elementTypes, Class<?> containerType) {
        return new ImmutableTypeResolver(rawType, elementTypes, containerType);
    }
}
