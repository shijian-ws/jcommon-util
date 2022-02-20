package com.github.jcommon.util;

import com.github.jcommon.constant.CommonConstant;
import com.github.jcommon.tuple.ImmutablePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 资源工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class PropertiesUtil {
    /**
     * 读取path对应的键值对
     */
    public static Map<String, String> read(String path) {
        if (Safes.isEmpty(path)) {
            return Collections.emptyMap();
        }
        return read(PropertiesUtil.class.getClassLoader(), new String[]{path}).getOrDefault(path, Collections.emptyMap());
    }

    /**
     * 读取path对应的键值对
     */
    public static Map<String, String> read(ClassLoader classLoader, String path) {
        if (Safes.isEmpty(path)) {
            return Collections.emptyMap();
        }
        Objects.requireNonNull(classLoader);
        return read(PropertiesUtil.class.getClassLoader(), new String[]{path}).getOrDefault(path, Collections.emptyMap());
    }

    /**
     * 读取每个path对应的键值对
     */
    public static Map<String, Map<String, String>> read(String... paths) {
        if (Safes.isEmpty(paths)) {
            return Collections.emptyMap();
        }
        return read(PropertiesUtil.class.getClassLoader(), paths);
    }

    /**
     * 读取每个path对应的键值对
     */
    public static Map<String, Map<String, String>> read(ClassLoader classLoader, String... paths) {
        if (Safes.isEmpty(paths)) {
            return Collections.emptyMap();
        }
        Objects.requireNonNull(classLoader);
        Map<String, Map<String, String>> result = Stream.of(paths)
                // 排除空白字符的路径
                .filter(StringUtil::isNotBlank)
                // 去重复
                .distinct()
                // 每个路径对应一个迭代器
                .collect(Collectors.toMap(Function.identity(), path -> {
                    Map<String, String> map = new HashMap<>(32);
                    for (URL url : getResources(classLoader, path)) {
                        map.putAll(read(url));
                    }
                    return Collections.unmodifiableMap(map);
                }));
        return Collections.unmodifiableMap(result);
    }

    /**
     * 获取资源文件路径
     */
    public static Iterable<URL> getResources(ClassLoader classLoader, String path) {
        if (Safes.isEmpty(path)) {
            return Collections.emptySet();
        }
        Objects.requireNonNull(classLoader);
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            if (resources != null) {
                return () -> IterableUtil.of(resources);
            }
            return Collections.emptySet();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 读取键值对
     */
    public static Map<String, String> read(URL url) {
        if (url == null) {
            return Collections.emptyMap();
        }
        try {
            return read(url.openStream());
        } catch (Exception e) {
            throw new IllegalArgumentException("Fail to read file: " + url.getFile(), e);
        }
    }

    /**
     * 读取键值对
     */
    public static Map<String, String> read(InputStream is) {
        if (is == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(Safes.of(readAsList(is)).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2)));
    }

    /**
     * 读取path对应的键值对, 延迟读取模式
     */
    public static Iterable<List<Map.Entry<String, String>>> readAsList(String path) {
        if (Safes.isEmpty(path)) {
            return Collections.emptySet();
        }
        return readAsList(PropertiesUtil.class.getClassLoader(), new String[]{path}).getOrDefault(path, Collections.emptySet());
    }

    /**
     * 读取path对应的键值对, 延迟读取模式
     */
    public static Iterable<List<Map.Entry<String, String>>> readAsList(ClassLoader classLoader, String path) {
        if (Safes.isEmpty(path)) {
            return Collections.emptySet();
        }
        Objects.requireNonNull(classLoader);
        return readAsList(PropertiesUtil.class.getClassLoader(), new String[]{path}).getOrDefault(path, Collections.emptySet());
    }

    /**
     * 读取每个path对应的键值对, 延迟读取模式
     */
    public static Map<String, Iterable<List<Map.Entry<String, String>>>> readAsList(String... paths) {
        if (Safes.isEmpty(paths)) {
            return Collections.emptyMap();
        }
        return readAsList(PropertiesUtil.class.getClassLoader(), paths);
    }

    /**
     * 读取每个path对应的键值对, 延迟读取模式
     */
    public static Map<String, Iterable<List<Map.Entry<String, String>>>> readAsList(ClassLoader classLoader, String... paths) {
        if (Safes.isEmpty(paths)) {
            return Collections.emptyMap();
        }
        Objects.requireNonNull(classLoader);
        Map<String, Iterable<List<Map.Entry<String, String>>>> result = Stream.of(paths)
                // 排除空白字符的路径
                .filter(StringUtil::isNotBlank)
                // 去重复
                .distinct()
                // 每个路径对应一个迭代器
                .collect(Collectors.toMap(Function.identity(), path ->
                        // 可迭代对象
                        () -> new Iterator<List<Map.Entry<String, String>>>() {
                            private Iterator<URL> iterator;

                            @Override
                            public boolean hasNext() {
                                if (iterator == null) {
                                    synchronized (this) {
                                        if (iterator == null) {
                                            iterator = getResources(classLoader, path).iterator();
                                        }
                                    }
                                }
                                return iterator.hasNext();
                            }

                            @Override
                            public List<Map.Entry<String, String>> next() {
                                return readAsList(iterator.next());
                            }
                        })
                );
        return Collections.unmodifiableMap(result);
    }

    /**
     * 读取键值对
     */
    public static List<Map.Entry<String, String>> readAsList(URL url) {
        if (url == null) {
            return Collections.emptyList();
        }
        try {
            return readAsList(url.openStream());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 读取键值对
     */
    public static List<Map.Entry<String, String>> readAsList(InputStream inputStream) {
        if (inputStream == null) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, String>> propertyList = new ArrayList<>(32);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Properties properties = new Properties();
            for (String line; (line = reader.readLine()) != null; ) {
                if ((line = filterLine(line)) == null) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                // 解析转义的多行配置
                flag: do {
                    for (int y = line.length() - 1, i = y; i >= 0; i--) {
                        if (line.charAt(i) != CommonConstant.REVERSE_SOLIDUS) {
                            // 不保留转义符
                            buf.append(line.substring(0, i + 1));
                            if (i == y) {
                                // 最后一个字符非转义符, 跳出最外层循环
                                break flag;
                            }
                            break;
                        }
                    }

                    // 存在转义符读取下一行
                    if ((line = filterLine(reader.readLine())) == null) {
                        // 下一行无字符
                        break;
                    }
                } while (true);
                // 利用Properties读取一个k/v配置, 防止重复
                properties.load(new StringReader(buf.toString().trim()));
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    if (key == null && value == null) {
                        continue;
                    }
                    propertyList.add(ImmutablePair.of(key, value));
                }
                properties.clear();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Fail to read inputStream: " + e.getMessage(), e);
        }
        return Collections.unmodifiableList(propertyList);
    }

    private static String filterLine(String line) {
        if (Safes.isEmpty(line)) {
            return null;
        }
        if ((line = line.trim()).isEmpty()) {
            // 空行
            return null;
        }
        int ci = line.indexOf(CommonConstant.NUMBER_SIGN);
        if (ci == 0) {
            // 全行注释
            return null;
        } else if (ci > 0) {
            // 截取注释之前
            return line.substring(0, ci).trim();
        }
        return line;
    }

    private PropertiesUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
