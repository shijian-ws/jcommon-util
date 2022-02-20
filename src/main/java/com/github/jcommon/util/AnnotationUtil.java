package com.github.jcommon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 注解工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class AnnotationUtil {
    /**
     * 查找指定注解, 循环向上查找
     */
    public static <T extends Annotation> T findAnnotation(AnnotatedElement type, Class<T> annClass) {
        if (type == null || annClass == null) {
            return null;
        }

        if (type instanceof Class) {
            return findAnnotation((Class<?>) type, annClass);
        }
        if (type instanceof Member) {
            return findAnnotation((Member) type, annClass);
        }
        return null;
    }

    /**
     * 查找类上的指定注解, 循环向上查找
     */
    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annClass) {
        if (clazz == null || annClass == null) {
            return null;
        }

        for (Class<?> targetClass = clazz; targetClass != null && targetClass != Object.class; targetClass = targetClass.getSuperclass()) {
            T annotation = targetClass.getAnnotation(annClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 查找属性或方法上或方法参数上的注解
     */
    public static <T extends Annotation> T findAnnotation(Member member, Class<T> annClass) {
        if (member == null || annClass == null) {
            return null;
        }

        if (member instanceof Field) {
            // 在属性上查找注解
            return ((Field) member).getAnnotation(annClass);
        }
        if (member instanceof Method) {
            return findAnnotation((Method) member, annClass);
        }
        return null;
    }

    /**
     * 查找方法上或方法参数上的注解
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T findAnnotation(Method method, Class<T> annClass) {
        if (method == null || annClass == null) {
            return null;
        }

        // 在方法上查找注解
        T annotation = method.getAnnotation(annClass);
        if (annotation != null) {
            return annotation;
        }
        // 在方法参数上查找注解, 一个方法每个参数都可能存在多个注解, 所以是二位数组
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (Safes.isNotEmpty(parameterAnnotations)) {
            for (Annotation[] annotations : parameterAnnotations) {
                // 一个参数一个参数的查找
                for (Annotation ann : annotations) {
                    if (annClass.isInstance(ann)) {
                        // 取一个找到的
                        return (T) ann;
                    }
                }
            }
        }
        // 未找到
        return null;
    }

    /**
     * 获取注解参数值
     *
     * @return 参数: annotation或func为null 则返回null
     */
    public static <T extends Annotation, V> V getValue(T annotation, Function<T, V> func) {
        if (annotation == null || func == null) {
            return null;
        }
        return func.apply(annotation);
    }

    /**
     * 将注解转换为对象
     */
    public static <T extends Annotation, V> V toObject(T annotation, Class<V> type) {
        if (annotation == null || type == null) {
            return null;
        }
        Map<String, Object> attrMap = Stream.of(annotation.annotationType().getDeclaredMethods()).collect(Collectors.toMap(Method::getName, method -> ReflectUtil.invoke(annotation, method)));
        return copyProperties(attrMap, type);
    }

    /**
     * 拷贝属性
     */
    public static <T extends Annotation, V> V copyProperties(Map<String, Object> map, Class<V> type) {
        if (Safes.isEmpty(map) || type == null) {
            return null;
        }
        return JsonUtil.toObject(JsonUtil.toString(map), type);
    }

    private AnnotationUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
