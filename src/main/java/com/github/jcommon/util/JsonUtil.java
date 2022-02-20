package com.github.jcommon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Json工具包
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public final class JsonUtil {
    /**
     * 对象转换为Json字符串
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    /**
     * Json字符串转换为对象, 可处理数组
     *
     * @return json为空则返回null, 转换失败返回null
     */
    public static <T> T toObject(String json, Class<T> type) {
        return toObject(json, (Type) type);
    }

    /**
     * Json字符串转换为对象, 可处理数组
     *
     * @return json为空则返回null, 转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, Type type) {
        Objects.requireNonNull(type, "类型不能为空");
        if (Safes.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json, type);
        } catch (Throwable e) {
            // LOGGER.error("Json字符串转换为{}失败: {}", type, e.toString());
        }
        return null;
    }

    /**
     * Json字符串转换为对象
     *
     * @return json为空则返回null, 转换失败返回null
     */
    public static <T> List<T> toList(String json, Class<T> type) {
        Objects.requireNonNull(type, "对象类型不能为空");
        if (Safes.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json, new TypeReference<List<T>>() {});
        } catch (Throwable e) {
            // LOGGER.error("Json字符串转换为List<{}>对象失败: {}", type, e.toString());
        }
        return null;
    }

    /**
     * Json字符串转换为Map
     *
     * @return json为空则返回null, 转换失败返回null
     */
    public static Map<String, String> toMap(String json) {
        return toMap(json, String.class, String.class);
    }

    /**
     * Json字符串转换为Map
     *
     * @return json为空则返回null, 转换失败返回null
     */
    public static <V> Map<String, V> toMap(String json, Class<V> valueType) {
        return toMap(json, String.class, valueType);
    }

    /**
     * Json字符串转换为对象
     *
     * @return json为空则返回null, 转换失败返回null
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        Objects.requireNonNull(keyType, "key对象类型不能为空");
        Objects.requireNonNull(valueType, "value对象类型不能为空");
        if (Safes.isEmpty(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json, new TypeReference<Map<K, V>>() {});
        } catch (Throwable e) {
            // LOGGER.error("Json字符串转换为Map<{}, {}>对象失败: {}", keyType, valueType, e.toString());
        }
        return null;
    }

    private JsonUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
