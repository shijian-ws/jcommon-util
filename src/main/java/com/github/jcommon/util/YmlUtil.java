package com.github.jcommon.util;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Objects;

/**
 * Yml工具包
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public final class YmlUtil {
    private static Yaml getYaml() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        return new Yaml(options);
    }

    /**
     * 对象转换为Yaml字符串
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return getYaml().dumpAsMap(obj);
    }

    /**
     * Yaml字符串转换为对象, 可处理数组
     *
     * @return yaml为空则返回null, 转换失败返回null
     */
    public static <T> T toObject(String yaml, Class<T> type) {
        Objects.requireNonNull(type, "类型不能为空");
        if (Safes.isEmpty(yaml)) {
            return null;
        }
        try {
            return getYaml().loadAs(yaml, type);
        } catch (Throwable e) {
            // LOGGER.error("Yaml字符串转换为{}失败: {}", type, e.toString());
            return null;
        }
    }

    /**
     * Yaml字符串转换为Map
     *
     * @return yaml为空则返回null, 转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String yaml) {
        if (Safes.isEmpty(yaml)) {
            return null;
        }
        try {
            return getYaml().loadAs(yaml, Map.class);
        } catch (Throwable e) {
            // LOGGER.error("Yaml字符串转换为Map对象失败: {}", e.toString());
            return null;
        }
    }

    private YmlUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
