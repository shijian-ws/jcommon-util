package com.github.jcommon.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 安全工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-09-14
 */
public final class Safes {
	private static final Stream<Object> EMPTY_STREAM = Stream.empty();
	private static final Supplier<Object> EMPTY_SUPPLIER = () -> null;
	private static final Map.Entry<Object, Object> EMPTY_ENTRY = new AbstractMap.SimpleImmutableEntry<>(null, null);

	/**
	 * 当参数为null返回一个不可变的空List集合, 当参数不为null时直接返回
	 */
	public static <T> List<T> of(List<T> list) {
		return Optional.ofNullable(list).orElse(Collections.emptyList());
	}

	/**
	 * 当参数为null返回一个不可变的空Set集合, 当参数不为null时直接返回
	 */
	public static <T> Set<T> of(Set<T> set) {
		return Optional.ofNullable(set).orElse(Collections.emptySet());
	}

	/**
	 * 当参数为null返回一个不可变的空SortedSet集合, 当参数不为null时直接返回
	 */
	public static <T> SortedSet<T> of(SortedSet<T> set) {
		return Optional.ofNullable(set).orElse(Collections.emptySortedSet());
	}

	/**
	 * 当参数为null返回一个不可变的空SortedSet集合, 当参数不为null时直接返回
	 */
	public static <T> NavigableSet<T> of(NavigableSet<T> set) {
		return Optional.ofNullable(set).orElse(Collections.emptyNavigableSet());
	}

	/**
	 * 当参数为null返回一个不可变的空Collection集合, 当参数不为null时直接返回
	 */
	public static <T> Collection<T> of(Collection<T> coll) {
		return Optional.ofNullable(coll).orElse(Collections.emptySet());
	}

	/**
	 * 当参数为null返回一个不可变的空迭代器, 当参数不为null时直接返回
	 */
	public static <T> Iterator<T> of(Iterator<T> iter) {
		return Optional.ofNullable(iter).orElse(Collections.emptyIterator());
	}

	/**
	 * 当参数为null返回一个不可变的空可迭代对象, 当参数不为null时直接返回
	 */
	public static <T> Iterable<T> of(Iterable<T> iter) {
		return Optional.ofNullable(iter).orElse(Collections::emptyIterator);
	}

	/**
	 * 当参数为null返回一个不可变的空Map集合, 当参数不为null时直接返回
	 */
	public static <K, V> Map<K, V> of(Map<K, V> map) {
		return Optional.ofNullable(map).orElse(Collections.emptyMap());
	}

	/**
	 * 当参数为null返回一个不可变的空SortedMap集合, 当参数不为null时直接返回
	 */
	public static <K, V> SortedMap<K, V> of(SortedMap<K, V> map) {
		return Optional.ofNullable(map).orElse(Collections.emptySortedMap());
	}

	/**
	 * 当参数为null返回一个不可变的空SortedMap集合, 当参数不为null时直接返回
	 */
	public static <K, V> NavigableMap<K, V> of(NavigableMap<K, V> map) {
		return Optional.ofNullable(map).orElse(Collections.emptyNavigableMap());
	}

	/**
	 * 当参数为null返回一个不可变的空Map.Entry对象, 当参数不为null时直接返回
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map.Entry<K, V> of(Map.Entry<K, V> entry) {
		return Optional.ofNullable(entry).orElse((Map.Entry<K, V>) EMPTY_ENTRY);
	}

	/**
	 * 当参数为null返回一个空Stream, 当参数不为null时直接返回
	 */
	@SuppressWarnings("unchecked")
	public static <T> Stream<T> of(Stream<T> stream) {
		return Optional.ofNullable(stream).orElse((Stream<T>) EMPTY_STREAM);
	}

	/**
	 * 当参数为null返回一个空Supplier, 当参数不为null时直接返回
	 */
	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> of(Supplier<T> supplier) {
		return Optional.ofNullable(supplier).orElse((Supplier<T>) EMPTY_SUPPLIER);
	}

	/**
	 * 当参数为null返回BigDecimal.ZERO, 当参数不为null时直接返回
	 */
	public static BigDecimal of(BigDecimal bigDecimal) {
		return of(bigDecimal, BigDecimal.ZERO);
	}

	/**
	 * 当参数为null返回指定值, 当参数不为null时直接返回
	 */
	public static BigDecimal of(BigDecimal bigDecimal, BigDecimal defaultValue) {
		return Optional.ofNullable(bigDecimal).orElse(defaultValue);
	}

	/**
	 * 当参数为null返回0, 当参数不为null时直接返回
	 */
	public static Integer of(Integer val) {
		return of(val, 0);
	}

	/**
	 * 当参数为null返回指定值, 当参数不为null时直接返回
	 */
	public static Integer of(Integer val, Integer defaultValue) {
		return Optional.ofNullable(val).orElse(defaultValue);
	}

	/**
	 * 当参数为null返回"", 当参数不为null时直接返回
	 */
	public static String of(String string) {
		return of(string, "");
	}

	/**
	 * 当source不为null时直接返回source, 如果source为null返回of(defaultValue)
	 */
	public static <T> T of(T source, T defaultValue) {
		return Optional.ofNullable(source).orElse(defaultValue);
	}

	/**
	 * 当source为null返回defaultValue, 当source不为null时直接返回source
	 */
	public static InputStream of(InputStream inputStream) {
		return Optional.ofNullable(inputStream).orElse(IOUtil.EMPTY_INPUT_STREAM);
	}

	/**
	 * 过滤排除集合中元素为null
	 *
	 * @return 如果list为null则返回不可变List集合对象
	 * 如果list为空集合或集合中没有null元素则返回原来List集合对象
	 * 如果存在null元素则返回新List集合对象, 新List集合对象为可变集合
	 */
	public static <T> List<T> filterNull(List<T> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		if (list.isEmpty()) {
			return list;
		}
		if (list.stream().allMatch(Objects::nonNull)) {
			return list;
		}
		return list.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取第一个元素
	 */
	public static <T> T first(Collection<T> coll) {
		if (coll == null || coll.isEmpty()) {
			return null;
		}
		if (coll instanceof LinkedList) {
			return ((LinkedList<T>) coll).peekFirst();
		}
		if (coll instanceof List) {
			return ((List<T>) coll).get(0);
		}
		if (coll instanceof Queue) {
			return ((Queue<T>) coll).peek();
		}
		if (coll instanceof SortedSet) {
			return ((SortedSet<T>) coll).first();
		}
		return first(coll.iterator());
	}

	/**
	 * 获取第一个元素
	 */
	public static <K, V> Map.Entry<K, V> first(Map<K, V> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		if (map instanceof NavigableMap) {
			return ((NavigableMap<K, V>) map).firstEntry();
		}
		return first(map.entrySet().iterator());
	}

	/**
	 * 获取第一个元素
	 */
	public static <T> T first(Iterable<T> iter) {
		if (iter == null) {
			return null;
		}
		if (iter instanceof Collection) {
			return first((Collection<T>) iter);
		}
		return first(iter.iterator());
	}

	/**
	 * 获取第一个元素
	 */
	public static <T> T first(Iterator<T> iter) {
		if (iter == null) {
			return null;
		}
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}

	/**
	 * 获取第一个元素
	 */
	public static <T> T first(Enumeration<T> enumeration) {
		if (enumeration == null) {
			return null;
		}
		if (enumeration.hasMoreElements()) {
			return enumeration.nextElement();
		}
		return null;
	}

	/**
	 * 获取第一个元素
	 */
	public static <E> E first(E[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		return array[0];
	}

	/**
	 * 获取最后一个元素
	 */
	public static <T> T last(Collection<T> coll) {
		if (coll == null || coll.isEmpty()) {
			return null;
		}
		if (coll instanceof LinkedList) {
			return ((LinkedList<T>) coll).peekLast();
		}
		if (coll instanceof List) {
			return ((List<T>) coll).get(coll.size() - 1);
		}
		if (coll instanceof Deque) {
			return ((Deque<T>) coll).peekLast();
		}
		if (coll instanceof SortedSet) {
			return ((SortedSet<T>) coll).last();
		}
		return last(coll.iterator());
	}

	/**
	 * 获取最后一个元素
	 */
	public static <K, V> Map.Entry<K, V> last(Map<K, V> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		if (map instanceof NavigableMap) {
			return ((NavigableMap<K, V>) map).lastEntry();
		}
		return last(map.entrySet().iterator());
	}

	/**
	 * 获取最后一个元素
	 */
	public static <T> T last(Iterable<T> iter) {
		if (iter == null) {
			return null;
		}
		if (iter instanceof Collection) {
			return last((Collection<T>) iter);
		}
		return last(iter.iterator());
	}

	/**
	 * 获取最后一个元素
	 */
	public static <T> T last(Iterator<T> iter) {
		if (iter == null) {
			return null;
		}
		T t = null;
		while (iter.hasNext()) {
			t = iter.next();
		}
		return t;
	}

	/**
	 * 获取最后一个元素
	 */
	public static <T> T last(Enumeration<T> enumeration) {
		if (enumeration == null) {
			return null;
		}
		T t = null;
		while (enumeration.hasMoreElements()) {
			t = enumeration.nextElement();
		}
		return t;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(short[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(int[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(float[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象是否为null或空数组
	 */
	public static boolean isEmpty(double[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static <T> boolean isNotEmpty(T[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(byte[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(short[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(int[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(long[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(float[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个数组对象不为null并且非空数组
	 */
	public static boolean isNotEmpty(double[] array) {
		return array != null && array.length > 0;
	}

	/**
	 * 判断一个集合对象是否为null或空集合
	 */
	public static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	/**
	 * 判断一个集合对象不为null并且非空集合
	 */
	public static boolean isNotEmpty(Collection<?> coll) {
		return coll != null && !coll.isEmpty();
	}

	/**
	 * 判断一个迭代器对象是否为null或没有下一个元素
	 */
	public static boolean isEmpty(Iterator<?> iterator) {
		return iterator == null || !iterator.hasNext();
	}

	/**
	 * 判断一个迭代器对象不为null并且有下一个元素
	 */
	public static boolean isNotEmpty(Iterator<?> iterator) {
		return iterator != null && iterator.hasNext();
	}

	/**
	 * 判断一个可迭代对象是否为null或没有元素
	 */
	public static boolean isEmpty(Iterable<?> iterable) {
		if (iterable instanceof Collection) {
			return isEmpty((Collection) iterable);
		}
		return iterable == null || isEmpty(iterable.iterator());
	}

	/**
	 * 判断一个可迭代对象不为null且含有元素
	 */
	public static boolean isNotEmpty(Iterable<?> iterable) {
		if (iterable instanceof Collection) {
			return isNotEmpty((Collection) iterable);
		}
		return iterable == null || isNotEmpty(iterable.iterator());
	}

	/**
	 * 判断一个Map对象是否为null或空Map
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 判断一个Map对象不为null并且非空Map
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return map != null && !map.isEmpty();
	}

	/**
	 * 判断一个字符串对象是否为null或空串
	 */
	public static boolean isEmpty(CharSequence val) {
		return val == null || val.length() == 0;
	}

	/**
	 * 判断一个字符串对象不为null并且非空串
	 */
	public static boolean isNotEmpty(CharSequence val) {
		return val != null && val.length() > 0;
	}

	private Safes() throws IllegalAccessException {
		throw new IllegalAccessException("不允许实例化");
	}
}
