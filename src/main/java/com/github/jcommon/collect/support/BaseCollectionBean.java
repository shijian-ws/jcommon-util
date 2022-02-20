package com.github.jcommon.collect.support;

import com.github.jcommon.collect.CollectionBean;
import com.github.jcommon.util.Safes;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 基本集合Bean, 支持Collection, Map, Dictionary操作
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
@SuppressWarnings("unchecked")
public class BaseCollectionBean implements CollectionBean {
    private final Class<?> collectionType;
    private final Object collection;
    private final AtomicInteger counterHolder = new AtomicInteger(0);

    protected BaseCollectionBean(Class<?> collectionType, Supplier<?> supplier) {
        this.collectionType = collectionType;
        this.collection = Safes.of(supplier).get();
    }

    protected BaseCollectionBean(Class<?> collectionType, Object collection) {
        this.collectionType = collectionType;
        this.collection = collection;
    }

    @Override
    public void add(Object key, Object value) {
        Object collection;
        if ((collection = this.collection) == null) {
            return;
        }

        if (collection instanceof Collection) {
            Collection<Object> coll = (Collection<Object>) collection;
            coll.add(value);
            counterHolder.incrementAndGet();
        } else if (collection instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) collection;
            map.put(key, value);
            counterHolder.incrementAndGet();
        } else if (collection instanceof Dictionary) {
            Dictionary<Object, Object> dict = (Dictionary<Object, Object>) collection;
            dict.put(key, value);
            counterHolder.incrementAndGet();
        }
    }

    @Override
    public int size() {
        return counterHolder.get();
    }

    @Override
    public <T> Class<T> getCollectionType() {
        return (Class<T>) collectionType;
    }

    @Override
    public <T> T getCollection() {
        return (T) collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseCollectionBean that = (BaseCollectionBean) o;
        return Objects.equals(collectionType, that.collectionType) &&
                Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionType, collection);
    }

    @Override
    public String toString() {
        return "BaseCollectionBean{" +
                "collectionType=" + collectionType +
                ", collection=" + collection +
                '}';
    }
}
