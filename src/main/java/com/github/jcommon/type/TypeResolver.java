package com.github.jcommon.type;

import java.lang.reflect.Type;

/**
 * 容器类型解析
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-23
 */
public interface TypeResolver extends Type {
    /**
     * 生成当前TypeResolver的原始Java类型
     */
    Type getRawType();

    /**
     * 获取元素类型集, 可能为具体类型:Class 或 嵌套引用:TypeResolver
     * 例如: Map<String, Object> -> containerType=Map, elementTypes=[String, Object]
     * 例如: Map<String, List<String>> -> containerType=Map, elementTypes=[String, TypeResolver{containerType=List, elementTypes=[String]}]
     */
    Type[] getElementTypes();

    /**
     * 获取元素类型
     * List<A> -> TypeResolver{elementTypes=[A],containerType=List}
     * List<A<B>> -> TypeResolver{elementTypes=[TypeResolver{elementTypes=[B],containerType=A}],containerType=List}
     */
    @SuppressWarnings("unchecked")
    default <T> Class<T> getElementClass() {
        Type[] elementTypes = getElementTypes();
        if (elementTypes == null || elementTypes.length == 0) {
            return null;
        }
        Type elementType = elementTypes[0];
        if (elementType instanceof Class) {
            // List<Class>> -> TypeResolver{elementTypes=[Class],containerType=List}
            return (Class<T>) elementType;
        }
        if (elementType instanceof TypeResolver) {
            // List<Class<Type>> -> TypeResolver{elementTypes=[TypeResolver{elementTypes=[Type],containerType=Class}],containerType=List}
            return (Class<T>) ((TypeResolver) elementType).getContainerType();
        }
        return null;
    }

    /**
     * 获取存储元素的容器类型, 如果无容器则为null
     */
    Class<?> getContainerType();
}
