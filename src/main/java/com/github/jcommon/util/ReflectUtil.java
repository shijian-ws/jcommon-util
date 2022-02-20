package com.github.jcommon.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 反射工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class ReflectUtil {
    /**
     * 标记可访问
     */
    public static boolean makeAccessible(AccessibleObject accessible) {
        if (accessible == null) {
            return false;
        }
        if (!Field.class.isInstance(accessible) && !Method.class.isInstance(accessible) && !Constructor.class.isInstance(accessible)) {
            // 非属性, 方法, 构造方法
            return false;
        }

        if (accessible.isAccessible()) {
            // 已经可访问
            return true;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getDeclaringClass().getModifiers()) && Modifier.isPublic(member.getModifiers())) {
                // 公共权限可以直接访问
                return true;
            }
        }

        try {
            accessible.setAccessible(true);
        } catch (Exception e) {
        }
        return accessible.isAccessible();
    }

    /**
     * 获取类或超类中满足指定条件的属性
     */
    public static List<Field> findFields(Class<?> clazz, boolean includeStatic) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        if (includeStatic) {
            // 无条件
            return findFields(clazz, null);
        }

        return findFields(clazz, field -> !Modifier.isStatic(field.getModifiers()));
    }

    /**
     * 获取类中满足指定条件的属性
     */
    public static List<Field> findFields(Class<?> clazz, Predicate<Field> filter) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> fieldList = new ArrayList<>(32);
        Class<?> targetClass = clazz;
        while (targetClass != null && targetClass != Object.class) {
            for (Field field : targetClass.getDeclaredFields()) {
                if (filter != null && !filter.test(field)) {
                    // 筛选条件不满足
                    continue;
                }
                fieldList.add(field);
            }

            // 指向父类
            targetClass = targetClass.getSuperclass();
        }

        return fieldList.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(fieldList);
    }

    /**
     * 获取指定方法反射对象
     */
    public static Method findMethod(Class<?> clazz, String methodName) {
        if (clazz != null && StringUtil.isNotBlank(methodName)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (Objects.equals(methodName, method.getName())) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获取类非抽象的指定类型具体方法(包含静态), 如果为Override方法则超类对应方法排除
     *
     * @param clazz
     * @param paramTypes 为null或空数组代表不做指定类型筛选, 为数组元素为null代表获取无参方法
     * @return 如果没有找到则返回空集合
     */
    public static List<Method> findConcreteMethods(Class<?> clazz, Class<?>... paramTypes) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        if (Safes.first(paramTypes) == null) {
            // 无条件
            return findConcreteMethods(clazz, (Predicate<Method>) null);

        }
        return findConcreteMethods(clazz, true, paramTypes);
    }

    /**
     * 获取类非抽象的指定类型方法或静态方法, 如果为Override方法则超类对应方法排除
     *
     * @param clazz
     * @param includeStatic 是否包含静态方法
     * @param paramTypes    为null或空数组代表不做指定类型筛选, 为数组元素为null代表获取无参方法
     * @return 如果没有找到则返回空集合
     */
    public static List<Method> findConcreteMethods(Class<?> clazz, boolean includeStatic, Class<?>... paramTypes) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        if (includeStatic && Safes.first(paramTypes) == null) {
            // 无条件
            return findConcreteMethods(clazz, (Predicate<Method>) null);
        }

        return findConcreteMethods(clazz, method ->
                // 需要非静态
                (includeStatic || !Modifier.isStatic(method.getModifiers())) &&
                        // 方法参数类型与帅选条件不一致
                        (Safes.first(paramTypes) == null || Arrays.equals(method.getParameterTypes(), paramTypes)));
    }

    /**
     * 获取类非抽象的指定类型具体方法或静态方法, 如果为Override方法则超类对应方法排除
     *
     * @param clazz
     * @param filter 过滤器, 如果为null则代表不过滤
     * @return 如果没有找到则返回空集合
     */
    public static List<Method> findConcreteMethods(Class<?> clazz, Predicate<Method> filter) {
        if (clazz == null || clazz.isInterface()) {
            return Collections.emptyList();
        }

        List<Method> methodList = new ArrayList<>(32);
        Class<?> targetClass = clazz;
        while (targetClass != null && targetClass != Object.class) {
            if (targetClass.isInterface()) {
                // 如果是接口那么超类全部都是接口
                break;
            }

            for (Method method : targetClass.getDeclaredMethods()) {
                int modifiers = targetClass.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    // 抽象
                    continue;
                }
                if (!Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)) {
                    // 非私有, 非静态, 可能为override
                    if (methodList.stream().anyMatch(m -> !Modifier.isPrivate(m.getModifiers()) &&
                            Objects.equals(m.getName(), method.getName()) &&
                            m.getParameterCount() == method.getParameterCount() &&
                            Arrays.equals(m.getParameterTypes(), method.getParameterTypes())
                    )) {
                        // 非私有且方法名称,参数一致
                        continue;
                    }
                }
                if (filter != null && !filter.test(method)) {
                    // 筛选条件不满足
                    continue;
                }
                methodList.add(method);
            }

            // 指向父类
            targetClass = targetClass.getSuperclass();
        }

        return methodList.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(methodList);
    }

    /**
     * 执行方法
     */
    public static void invoke(Method method, Object... args) {
        invoke(null, method, args);
    }

    /**
     * 执行方法
     */
    public static Object invoke(Object object, Method method, Object... args) {
        Objects.requireNonNull(method);
        makeAccessible(method);
        try {
            if (object == null || Modifier.isStatic(method.getModifiers())) {
                return method.invoke(null, args);
            }
            return method.invoke(object, args);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            if (e instanceof InvocationTargetException && e.getCause() == null) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            throw new RuntimeException("Failed method invoke: ", e);
        }
    }

    /**
     * 属性赋值
     */
    public static void setValue(Field field, Object arg) {
        setValue(null, field, arg);
    }

    /**
     * 属性赋值
     */
    public static void setValue(Object object, Field field, Object arg) {
        Objects.requireNonNull(field);
        makeAccessible(field);
        try {
            if (object == null || Modifier.isStatic(field.getModifiers())) {
                field.set(field.getDeclaringClass(), arg);
                return;
            }
            field.set(object, arg);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed field set value: ", e);
        }
    }

    /**
     * 实例化
     */
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        try {
            Constructor<T> constructor = getConstructor(clazz, args);
            makeAccessible(constructor);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getConstructor(Class<T> clazz, Object[] args) throws NoSuchMethodException {
        if (Safes.isEmpty(args)) {
            // 无参构造
            return clazz.getDeclaredConstructor();
        }

        int length = args.length;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        List<Constructor<?>> list = Stream.of(constructors)
                // 参数个数, 类型匹配
                .filter(c -> c.getParameterCount() == length &&
                        IntStream.range(0, length)
                                // 如果args元素有为null代表可任意匹配
                                .allMatch(i -> args[i] == null || c.getParameterTypes()[i].isInstance(args[i])))
                .collect(Collectors.toList());
        if (list.size() == 1) {
            return (Constructor<T>) list.get(0);
        }

        // TODO 存在多个, 多态形式, 需要取一个最合适的
        return (Constructor<T>) Safes.first(list);
    }


    /**
     * 判断是否基本数据类型或基本数据类型的包装类
     */
    public static boolean isPrimitiveOrWrap(Class<?> type) {
        if (type == null) {
            return false;
        }

        return type.isPrimitive() ||
                type == Boolean.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Short.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Float.class ||
                type == Double.class;
    }

    /**
     * 判断是否为数组类型
     */
    public static boolean isArray(Class<?> type) {
        return type != null && type.isArray();
    }

    private static final Map<Class<?>, Map<Integer, Set<Class<?>>>> CACHE_LEVEL_CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 获取当前Class超类, 0为当前参数Class, 1:为父Class, 2:父父Clsas, ...
     *
     * @return 如果参数为空则返回空Map
     */
    public static Map<Integer, Set<Class<?>>> getLevelClassMap(Class<?> targetClass) {
        if (targetClass == null) {
            return Collections.emptyMap();
        }
        return CACHE_LEVEL_CLASS_MAP.computeIfAbsent(targetClass, key -> {
            Map<Integer, Set<Class<?>>> levelClassMap = new LinkedHashMap<>(16);
            // 放入0阶
            levelClassMap.put(0, Collections.singleton(targetClass));

            Set<Class<?>> allClassSet = new HashSet<>(16);
            for (int level = 1; ; ) {
                // 寻找上一阶级的所有父类
                Set<Class<?>> levelClassSet = levelClassMap.getOrDefault(level - 1, Collections.emptySet()).stream()
                        .map(clazz -> clazz.getSuperclass() == Object.class ?
                                Stream.of(clazz.getInterfaces()) :
                                Stream.concat(Stream.of(clazz.getInterfaces()), Stream.of(clazz.getSuperclass())))
                        .flatMap(Function.identity())
                        .filter(Objects::nonNull)
                        // 不包含已经存在的
                        .filter(clazz -> !allClassSet.contains(clazz))
                        // 添加到应存在的集合中
                        .peek(allClassSet::add)
                        .collect(Collectors.toSet());
                if (levelClassSet.isEmpty()) {
                    break;
                }
                levelClassMap.put(level++, Collections.unmodifiableSet(levelClassSet));
            }
            return Collections.unmodifiableMap(levelClassMap);
        });
    }

    /**
     * 获取superClass在clazz的继承体系的第几层, 例如: clazz的父类或接口包含superClass返回1, clazz的父类或接口的父类或接口包含superClass返回2, ...
     *
     * @return 参数clazz或superClass为null或非无关联则返回null
     */
    public static Integer getLevel(Class<?> clazz, Class<?> superClass) {
        if (superClass == null || clazz == null || !superClass.isAssignableFrom(clazz)) {
            return null;
        }

        Map<Integer, Set<Class<?>>> levelClassMap = getLevelClassMap(clazz);
        for (Map.Entry<Integer, Set<Class<?>>> entry : levelClassMap.entrySet()) {
            if (entry.getValue().contains(superClass)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private ReflectUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
