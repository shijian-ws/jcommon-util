package com.github.jcommon.type;

import com.github.jcommon.util.Assert;
import com.github.jcommon.util.Safes;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Type解析工具, 参照: {@see org.apache.ibatis.reflection.TypeParameterResolver}
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class TypeResolverUtil {
    /**
     * 检查是否全部为真实类型
     */
    public static void checkAllActualType(TypeResolver reference) {
        Assert.notNull(reference, "reference不能为null");
        List<TypeResolver> searchArgs = Collections.singletonList(reference);
        while (Safes.isNotEmpty(searchArgs)) {
            List<TypeResolver> nextArgs = new ArrayList<>(16);
            for (TypeResolver arg : searchArgs) {
                Type rawType = arg.getRawType();
                if (rawType instanceof TypeVariable) {

                }
                if (Safes.isEmpty(arg.getElementTypes())) {
                    continue;
                }
                for (Type type : arg.getElementTypes()) {
                    if (type instanceof TypeResolver) {
                        nextArgs.add((TypeResolver) type);
                    }
                }
            }
            searchArgs = nextArgs;
        }
    }

    /**
     * 解析属性值真实类型
     */
    public static TypeResolver resolverActualType(Field field) {
        if (field == null) {
            return null;
        }
        Class<?> declaringClass = field.getDeclaringClass();
        return resolverType(field.getGenericType(), declaringClass, declaringClass);
    }

    /**
     * 解析属性值真实类型, 如果存在泛型变量则会在srcType中寻找真实类型
     */
    public static TypeResolver resolverActualType(Field field, Type srcType) {
        if (field == null) {
            return null;
        }
        return resolverType(field.getGenericType(), field.getDeclaringClass(), srcType);
    }

    /**
     * 解析方法返回值的真实类型, 如果方法无返回值返回null
     */
    public static TypeResolver resolverActualReturnType(Method method) {
        Type genericReturnType;
        if (method == null || (genericReturnType = method.getGenericReturnType()) == null) {
            return null;
        }

        Class<?> declaringClass = method.getDeclaringClass();
        return resolverType(genericReturnType, declaringClass, declaringClass);
    }

    /**
     * 解析方法返回值的真实类型, 如果方法无返回值返回null, 如果存在泛型变量则会在srcType中寻找真实类型
     */
    public static TypeResolver resolverActualReturnType(Method method, Type srcType) {
        Type genericReturnType;
        if (method == null || (genericReturnType = method.getGenericReturnType()) == null) {
            return null;
        }
        return resolverType(genericReturnType, method.getDeclaringClass(), srcType);
    }

    /**
     * 解析方法第一个参数的真实类型, 如果方法参数为0则返回null
     */
    public static TypeResolver resolverActualParamType(Method method) {
        if (method == null || method.getParameterCount() == 0) {
            return null;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        return resolverType(method.getGenericParameterTypes()[0], declaringClass, declaringClass);
    }

    /**
     * 解析类泛型的真实没写
     */
    public static TypeResolver resolverActualClassGenericType(Class<?> clazz) {
        return resolverType(clazz.getGenericSuperclass(), clazz, clazz);
    }

    /**
     * 解析方法第一个参数的真实类型, 如果方法参数为0则返回null, 如果存在泛型变量则会在srcType中寻找真实类型
     */
    public static TypeResolver resolverActualParamType(Method method, Type srcType) {
        if (method == null || method.getParameterCount() == 0) {
            return null;
        }
        return resolverType(method.getGenericParameterTypes()[0], method.getDeclaringClass(), srcType);
    }

    /**
     * 解析方法所有参数的真实类型, 如果方法参数为0则返回null
     */
    public static TypeResolver[] resolverActualParamTypes(Method method) {
        if (method == null || method.getParameterCount() == 0) {
            return null;
        }
        return resolverActualParamTypes(method, method.getDeclaringClass());
    }

    /**
     * 解析方法所有参数的真实类型, 如果方法参数为0则返回null, 如果存在泛型变量则会在srcType中寻找真实类型
     */
    public static TypeResolver[] resolverActualParamTypes(Method method, Type srcType) {
        if (method == null || method.getParameterCount() == 0) {
            return null;
        }
        Type[] genericParamTypes = method.getGenericParameterTypes();
        TypeResolver[] array = new TypeResolver[method.getParameterCount()];
        for (int i = 0, y = array.length; i < y; i++) {
            array[i] = resolverType(genericParamTypes[i], method.getDeclaringClass(), srcType);
        }
        return array;
    }

    private static TypeResolver resolverType(Type genericType, Class<?> declaringClass, Type srcType) {
        return resolverType(genericType, genericType, declaringClass, srcType);
    }

    /**
     * 解析处理
     *
     * @param rawType        原始类型
     * @param genericType    泛型类型
     * @param declaringClass 声明泛型类型的Class
     * @param srcType        指定泛型的真实类型
     * @return
     */
    private static TypeResolver resolverType(Type rawType, Type genericType, Class<?> declaringClass, Type srcType) {
        Assert.notNull(rawType, "rawType must be not null");
        Assert.notNull(genericType, "genericType must be not null");
        Assert.notNull(declaringClass, "declaringClass must be not null");
        Assert.notNull(srcType, "srcType must be not null");

        if (genericType instanceof TypeVariable) {
            return resolverTypeVariable(rawType, (TypeVariable<?>) genericType, declaringClass, srcType);
        }
        if (genericType instanceof ParameterizedType) {
            return resolverParameterizedType(rawType, (ParameterizedType) genericType, declaringClass, srcType);
        }
        if (genericType instanceof GenericArrayType) {
            return resolverGenericArrayType(rawType, (GenericArrayType) genericType, declaringClass, srcType);
        }
        if (genericType instanceof WildcardType) {
            return resolverWildcardType(rawType, (WildcardType) genericType, declaringClass, srcType);
        }
        if (genericType instanceof Class) {
            return resolverClass(rawType, (Class<?>) genericType);
        }
        throw new IllegalArgumentException("The genericType must be TypeVariable or ParameterizedType or GenericArrayType or Class, but was: " + genericType.getClass());
    }

    /**
     * 处理泛型变量, 因为类型变量需要从实现
     */
    private static TypeResolver resolverTypeVariable(Type rawType, TypeVariable<?> typeVar, Class<?> declaringClass, Type srcType) {
        Class<?> targetClass;
        if (srcType instanceof Class) {
            targetClass = (Class<?>) srcType;
        } else if (srcType instanceof ParameterizedType) {
            targetClass = (Class<?>) ((ParameterizedType) srcType).getRawType();
        } else {
            throw new IllegalArgumentException("The srcType must be Class or ParameterizedType, but was: " + srcType.getClass());
        }

        if (targetClass == declaringClass) {
            if (declaringClass == srcType) {
                // 泛型变量没有对应的真实类型
                return ImmutableTypeResolver.of(rawType, (Type[]) null, null);
            }
            Type[] bounds = typeVar.getBounds();
            TypeResolver reference = buildTypeResolver(rawType, getBoundType(bounds, typeVar, declaringClass, srcType));
            if (reference != null) {
                return reference;
            }
            // LOGGER.warn("not actual class : typeVar ->" + Arrays.toString(bounds));
            return ImmutableTypeResolver.of(rawType, (Type[]) null, null);
        }

        // 从srcType寻找泛型变量的真实类型
        TypeResolver reference = searchSuperType(rawType, typeVar, declaringClass, srcType, targetClass, targetClass.getGenericSuperclass());
        if (reference == null) {
            reference = searchSuperTypes(rawType, typeVar, declaringClass, srcType, targetClass, targetClass.getGenericInterfaces());
        }
        if (reference != null) {
            return reference;
        }

        // 未定义的泛型变量, 可能为方法自定义, 尝试取一次边界
        return buildTypeResolver(rawType, getBoundType(typeVar.getBounds(), typeVar, declaringClass, srcType));
    }

    private static Type getBoundType(Type[] bounds, Type rawType, Class<?> declaringClass, Type srcType) {
        if (Safes.isEmpty(bounds)) {
            return null;
        }
        if (bounds.length == 1) {
            return bounds[0];
        }
        // 交集
        Type[] elementTypes = Stream.of(bounds)
                // 非Class则再次解析一下
                .map(bound -> bound instanceof Class ? bound : resolverType(rawType, bound, declaringClass, srcType))
                .toArray(Type[]::new);
        return ImmutableTypeResolver.of(rawType, elementTypes, null);
    }

    private static TypeResolver buildTypeResolver(Type rawType, Type actualType) {
        if (actualType instanceof Class) {
            Class<?> containerType = null;
            if (rawType instanceof GenericArrayType) {
                // 泛型变量数组
                containerType = Array.newInstance((Class<?>) actualType, 0).getClass();
            }
            return ImmutableTypeResolver.of(rawType, actualType, containerType);
        }
        if (actualType instanceof TypeResolver) {
            TypeResolver reference = (TypeResolver) actualType;
            if (rawType instanceof GenericArrayType) {
                // 泛型变量数组
                return ImmutableTypeResolver.of(rawType, reference, null);
            }
            return reference;
        }
        return null;
    }

    /**
     * 检索超类
     */
    private static TypeResolver searchSuperTypes(Type rawType, TypeVariable<?> typeVar, Class<?> declaringClass, Type srcType, Class<?> targetClass, Type... superTypes) {
        for (Type superType : superTypes) {
            TypeResolver reference = searchSuperType(rawType, typeVar, declaringClass, srcType, targetClass, superType);
            if (reference != null) {
                return reference;
            }
        }
        return null;
    }

    /**
     * 检索超类类型, 根据泛型擦除擦除原理检索superType可能发现泛型真实类型
     */
    private static TypeResolver searchSuperType(Type rawType, TypeVariable<?> typeVar, Class<?> declaringClass, Type srcType, Class<?> targetClass, Type superType) {
        if (superType instanceof Class && declaringClass.isAssignableFrom((Class<?>) superType)) {
            // 超类类型是具体类, 且声明泛型类的子类
            return resolverTypeVariable(rawType, typeVar, declaringClass, superType);
        } else if (superType instanceof ParameterizedType) {
            // 超类类型是泛型类型
            ParameterizedType superGenericType = (ParameterizedType) superType;
            // 超类类型所属的超类
            Class<?> superClass = (Class<?>) superGenericType.getRawType();
            // 超类上的泛型变量
            TypeVariable<? extends Class<?>>[] superTypeVars = superClass.getTypeParameters();
            if (srcType instanceof ParameterizedType) {
                superGenericType = replaceTypeVariable(superGenericType, (ParameterizedType) srcType, targetClass);
            }
            // 超类上的泛型的真实类型, 当superType为declaringClass的超类才能为真实类型, 否则可能也是泛型变量, 因为泛型擦除
            Type[] superAsActualTypes = superGenericType.getActualTypeArguments();
            if (declaringClass == superClass) {
                for (int i = 0, j = superTypeVars.length; i < j; i++) {
                    if (typeVar == superTypeVars[i]) {
                        Type actualType = superAsActualTypes[i];
                        TypeResolver reference = buildTypeResolver(rawType, actualType);
                        if (reference != null) {
                            return reference;
                        }
                        // LOGGER.warn("非真实类型: typeVar ->" + actualType.getClass());
                        return ImmutableTypeResolver.of(rawType, (Type[]) null, null);
                    }
                }
            }
            return null;
        }
        // LOGGER.warn("未知类型: superType ->" + superType.getClass());
        return null;
    }

    /**
     * 替换泛型变量为真实类型
     *
     * @param parameterizedType
     * @param srcType
     * @param srcClass
     * @return
     */
    private static ParameterizedType replaceTypeVariable(ParameterizedType parameterizedType, ParameterizedType srcType, Class<?> srcClass) {
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        if (Stream.of(typeArgs).noneMatch(TypeVariable.class::isInstance)) {
            // 没有泛型变量
            return parameterizedType;
        }

        Type[] srcTypeArgs = srcType.getActualTypeArguments();
        TypeVariable<?>[] srcTypeVars = srcClass.getTypeParameters();

        Type[] newTypeArgs = Stream.of(typeArgs)
                .map(arg -> {
                    if (arg instanceof TypeVariable) {
                        TypeVariable<?> typeVar = (TypeVariable<?>) arg;
                        for (int i = 0, j = srcTypeVars.length; i < j; i++) {
                            if (typeVar == srcTypeVars[i]) {
                                return srcTypeArgs[i];
                            }
                        }
                    }
                    return arg;
                })
                .toArray(Type[]::new);
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return newTypeArgs;
            }

            @Override
            public Type getRawType() {
                return parameterizedType.getRawType();
            }

            @Override
            public Type getOwnerType() {
                return parameterizedType.getOwnerType();
            }
        };
    }

    /**
     * 处理参数泛型
     */
    private static TypeResolver resolverParameterizedType(Type rawType, ParameterizedType parameterizedType, Class<?> declaringClass, Type srcType) {
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        Type[] elementTypes = Stream.of(typeArgs)
                .map(arg -> arg instanceof Class ? arg : resolverType(arg, declaringClass, srcType))
                .toArray(Type[]::new);
        Class<?> containerType = (Class<?>) parameterizedType.getRawType();
        if (rawType instanceof GenericArrayType) {
            // 源类型是数组
            containerType = Array.newInstance(containerType, 0).getClass();
        }
        return ImmutableTypeResolver.of(rawType, elementTypes, containerType);
    }

    /**
     * 处理泛型数组泛型
     */
    private static TypeResolver resolverGenericArrayType(Type rawType, GenericArrayType genericArrayType, Class<?> declaringClass, Type srcType) {
        return resolverType(rawType, genericArrayType.getGenericComponentType(), declaringClass, srcType);
    }

    /**
     * 处理真实类型
     */
    private static TypeResolver resolverClass(Type rawType, Class<?> clazz) {
        if (clazz.isArray()) {
            // 数组
            return ImmutableTypeResolver.of(rawType, clazz.getComponentType(), clazz);
        }
        // 普通类型
        return ImmutableTypeResolver.of(rawType, clazz, null);
    }

    /**
     * 处理边界泛型边界
     */
    private static TypeResolver resolverWildcardType(Type rawType, WildcardType wildcardType, Class<?> declaringClass, Type srcType) {
        // Field 不能定义边界
        Type[] upperBounds = wildcardType.getUpperBounds();
        Type type;
        if (Safes.isNotEmpty(upperBounds)) {
            type = getBoundType(upperBounds, wildcardType, declaringClass, srcType);
        } else {
            type = getBoundType(wildcardType.getLowerBounds(), wildcardType, declaringClass, srcType);
        }
        if (type instanceof TypeResolver) {
            return (TypeResolver) type;
        }
        return resolverType(rawType, type, declaringClass, srcType);
    }

    private TypeResolverUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
