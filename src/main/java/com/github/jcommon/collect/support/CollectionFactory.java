package com.github.jcommon.collect.support;

import com.github.jcommon.collect.CollectionBean;
import com.github.jcommon.collect.support.ArrayCollectionBean;
import com.github.jcommon.collect.support.BaseCollectionBean;
import com.github.jcommon.util.ReflectUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Supplier;

/**
 * 集合创建工厂
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public final class CollectionFactory {
    /**
     * 集合创建函数Map
     */
    private static final Map<Class<?>, Supplier<?>> APPROXIMABLE_COLLECTION_SUPPLIER_MAP;
    /**
     * 缓存指定类型集合的创建函数
     */
    private static final Map<Class<?>, Supplier<?>> CACHE_COLLECTION_SUPPLIER_MAP = new ConcurrentHashMap<>();

    static {
        Map<Class<?>, Supplier<?>> approximableCollectionMap = new LinkedHashMap<>(16);

        // List
        Supplier<ArrayList> arrayListSupplier = () -> new ArrayList<>(16);
        Supplier<LinkedList> linkedListSupplier = LinkedList::new;
        Supplier<CopyOnWriteArrayList> copyOnWriteArrayListSupplier = CopyOnWriteArrayList::new;
        Supplier<Vector> vectorSupplier = () -> new Vector<>(16);

        // Queue
        Supplier<PriorityQueue> priorityQueueSupplier = () -> new PriorityQueue<>(16);
        Supplier<ArrayDeque> arrayDequeSupplier = () -> new ArrayDeque<>(16);
        Supplier<ArrayBlockingQueue> arrayBlockingQueueSupplier = () -> new ArrayBlockingQueue<>(16);
        Supplier<PriorityBlockingQueue> priorityBlockingQueueSupplier = () -> new PriorityBlockingQueue<>(16);
        Supplier<LinkedBlockingQueue> linkedBlockingQueueSupplier = () -> new LinkedBlockingQueue<>(16);
        Supplier<LinkedBlockingDeque> linkedBlockingDequeSupplier = () -> new LinkedBlockingDeque<>(16);

        // Set
        Supplier<HashSet> hashSetSupplier = () -> new HashSet<>(16);
        Supplier<LinkedHashSet> linkedHashSetSupplier = () -> new LinkedHashSet<>(16);
        Supplier<NavigableSet> navigableSetSupplier = TreeSet::new;
        Supplier<CopyOnWriteArraySet> copyOnWriteArraySetSupplier = CopyOnWriteArraySet::new;

        // Map
        Supplier<HashMap> hashMapSupplier = () -> new HashMap<>(16);
        Supplier<LinkedHashMap> linkedHashMapSupplier = () -> new LinkedHashMap<>(16);
        Supplier<TreeMap> treeMapSupplier = TreeMap::new;
        Supplier<ConcurrentHashMap> concurrentHashMapSupplier = () -> new ConcurrentHashMap<>(16);
        Supplier<ConcurrentSkipListMap> concurrentSkipListMapSupplier = ConcurrentSkipListMap::new;
        Supplier<IdentityHashMap> identityHashMapSupplier = IdentityHashMap::new;
        Supplier<WeakHashMap> weakHashMapSupplier = WeakHashMap::new;

        // Dictionary
        Supplier<Hashtable> hashtableSupplier = () -> new Hashtable<>(16);
        Supplier<Properties> propertiesSupplier = Properties::new;

        approximableCollectionMap.put(Collection.class, arrayListSupplier);

        // List
        approximableCollectionMap.put(List.class, arrayListSupplier);
        approximableCollectionMap.put(ArrayList.class, arrayListSupplier);
        approximableCollectionMap.put(LinkedList.class, linkedListSupplier);
        approximableCollectionMap.put(CopyOnWriteArrayList.class, copyOnWriteArrayListSupplier);
        approximableCollectionMap.put(Vector.class, vectorSupplier);

        // Queue
        approximableCollectionMap.put(Queue.class, linkedListSupplier);
        approximableCollectionMap.put(Deque.class, linkedListSupplier);
        approximableCollectionMap.put(BlockingQueue.class, arrayBlockingQueueSupplier);
        approximableCollectionMap.put(BlockingDeque.class, linkedBlockingDequeSupplier);
        approximableCollectionMap.put(PriorityQueue.class, priorityQueueSupplier);
        approximableCollectionMap.put(ArrayDeque.class, arrayDequeSupplier);
        approximableCollectionMap.put(ArrayBlockingQueue.class, arrayBlockingQueueSupplier);
        approximableCollectionMap.put(PriorityBlockingQueue.class, priorityBlockingQueueSupplier);
        approximableCollectionMap.put(LinkedBlockingQueue.class, linkedBlockingQueueSupplier);
        approximableCollectionMap.put(LinkedBlockingDeque.class, linkedBlockingDequeSupplier);

        // Set
        approximableCollectionMap.put(Set.class, hashSetSupplier);
        approximableCollectionMap.put(SortedSet.class, navigableSetSupplier);
        approximableCollectionMap.put(NavigableSet.class, navigableSetSupplier);
        approximableCollectionMap.put(HashSet.class, hashSetSupplier);
        approximableCollectionMap.put(LinkedHashSet.class, linkedHashSetSupplier);
        approximableCollectionMap.put(CopyOnWriteArraySet.class, copyOnWriteArraySetSupplier);

        // Map
        approximableCollectionMap.put(Map.class, hashMapSupplier);
        approximableCollectionMap.put(SortedMap.class, treeMapSupplier);
        approximableCollectionMap.put(NavigableMap.class, treeMapSupplier);
        approximableCollectionMap.put(ConcurrentMap.class, concurrentHashMapSupplier);
        approximableCollectionMap.put(ConcurrentNavigableMap.class, concurrentSkipListMapSupplier);
        approximableCollectionMap.put(HashMap.class, hashMapSupplier);
        approximableCollectionMap.put(LinkedHashMap.class, linkedHashMapSupplier);
        approximableCollectionMap.put(IdentityHashMap.class, identityHashMapSupplier);
        approximableCollectionMap.put(WeakHashMap.class, weakHashMapSupplier);

        // Dictionary
        approximableCollectionMap.put(Dictionary.class, hashtableSupplier);
        approximableCollectionMap.put(Hashtable.class, hashtableSupplier);
        approximableCollectionMap.put(Properties.class, propertiesSupplier);

        APPROXIMABLE_COLLECTION_SUPPLIER_MAP = Collections.unmodifiableMap(approximableCollectionMap);
    }

    /**
     * 根据集合类型获取集合创建函数
     */
    @SuppressWarnings("unchecked")
    private static <T> Supplier<T> getSupplier(Class<T> collType) {
        if (collType == null) {
            return null;
        }

        // 先缓存中获取
        return (Supplier<T>) CACHE_COLLECTION_SUPPLIER_MAP.computeIfAbsent(collType, key -> {
            // 未命中缓存
            Supplier<?> get = APPROXIMABLE_COLLECTION_SUPPLIER_MAP.get(key);
            if (get != null) {
                // 找到具体集合类型对应的创建函数
                return get;
            }
            // 未找到, 进行相似匹配
            Map.Entry<Class<?>, Supplier<?>> entry = APPROXIMABLE_COLLECTION_SUPPLIER_MAP.entrySet().stream()
                    // 筛选出指定集合的子类型
                    .filter(en -> key.isAssignableFrom(en.getKey()))
                    // 取继承层次最近的
                    .min(Comparator.comparing(en -> Optional.ofNullable(ReflectUtil.getLevel(en.getKey(), key)).orElse(Integer.MAX_VALUE))).orElse(null);
            return entry == null ? null : entry.getValue();
        });
    }

    /**
     * 是否支持集合类型的创建
     */
    public static boolean isSupportType(Class<?> collType) {
        return collType != null && getSupplier(collType) != null;
    }

    /**
     * 创建集合对象, 如果未知集合类型则找找最匹配的, 支持创建数组
     */
    public static <T> CollectionBean of(Class<T> collType) {
        if (collType == null) {
            return null;
        }

        if (collType.isArray()) {
            return new ArrayCollectionBean(collType);
        }
        Supplier<T> supplier = getSupplier(collType);
        return supplier == null ? null : new BaseCollectionBean(collType, supplier);
    }

    private CollectionFactory() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
