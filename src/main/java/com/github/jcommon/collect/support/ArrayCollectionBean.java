package com.github.jcommon.collect.support;

import com.github.jcommon.util.ReflectUtil;
import com.github.jcommon.util.Safes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 数组类型集合Bean
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
@SuppressWarnings("unchecked")
public final class ArrayCollectionBean extends BaseCollectionBean {
    public ArrayCollectionBean(Class<?> type) {
        super(type, new ArrayList<>(16));
        if (!ReflectUtil.isArray(type)) {
            throw new IllegalArgumentException("type must be array class");
        }
    }

    @Override
    public <T> T getCollection() {
        // 获取临时ArrayList容器
        List<Object> list = Safes.of((List<Object>) super.getCollection());
        // 创建数组
        Object array = Array.newInstance(this.getCollectionType().getComponentType(), list.size());
        // 赋值
        IntStream.range(0, list.size()).forEach(i -> Array.set(array, i, list.get(i)));
        return (T) array;
    }
}
