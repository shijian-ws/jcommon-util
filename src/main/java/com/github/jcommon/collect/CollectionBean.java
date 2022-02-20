package com.github.jcommon.collect;

/**
 * 集合Bean
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public interface CollectionBean {
    /**
     * 集合添加元素, Collection, Array类只添加value
     */
    void add(Object key, Object value);

    /**
     * 集合存储元素数量
     */
    int size();

    /**
     * 集合是否没有存储元素
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 获取当前的集合类型
     */
    <T> Class<T> getCollectionType();

    /**
     * 获取集合对象
     */
    <T> T getCollection();
}
