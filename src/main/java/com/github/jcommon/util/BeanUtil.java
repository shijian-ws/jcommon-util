package com.github.jcommon.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Bean工厂
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public final class BeanUtil {
	private static final MapperFacade MAPPER_FACADE;

	static {
		DefaultMapperFactory factory = new DefaultMapperFactory.Builder()
				//.mapNulls(false)
				.build();
		MAPPER_FACADE = factory.getMapperFacade();
	}

	/**
	 * 将src的属性值复覆盖到dest中, 只要两个对象有共同的属性名即可覆盖, 即使src属性值为null而dest对应属性有值也会被覆盖为null
	 * 注意: 如果src为null则不做任何操作
	 * 深度拷贝
	 */
	public static void copyProperties(Object dest, Object src) {
		if (src == null) {
			return;
		}
		MAPPER_FACADE.map(src, dest);
	}

	/**
	 * 深度拷贝一个对象
	 * 注意: 如果src为null则返回null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copy(T src) {
		if (src == null) {
			return null;
		}
		return (T) MAPPER_FACADE.map(src, src.getClass());
	}

	/**
	 * 将src的属性值深度拷贝到一个对象中
	 * 注意: 如果src为null则返回null
	 */
	public static <T> T copy(Object src, Class<T> to) {
		if (src == null) {
			return null;
		}
		Objects.requireNonNull(to, "to类型不能为null");
		return MAPPER_FACADE.map(src, to);
	}

	/**
	 * 将src中from与to交集属性的属性值深度拷贝到一个对象中
	 * 注意: 如果src为null则返回null
	 */
	public static <T> T copy(Object src, Class<?> from, Class<T> to) {
		if (src == null) {
			return null;
		}
		Objects.requireNonNull(from, "from类型不能为null");
		Objects.requireNonNull(to, "to类型不能为null");
		Object newObj = MAPPER_FACADE.map(src, from);
		return MAPPER_FACADE.map(newObj, to);
	}

	/**
	 * 将source中每个元素深度拷贝到一个新集合中
	 * 注意: 如果source为null则返回null
	 */
	public static <T> List<T> copyAsList(Iterable<Object> source, Class<T> type) {
		if (source == null) {
			return null;
		}
		return MAPPER_FACADE.mapAsList(source, type);
	}

	/**
	 * 将source中每个元素深度拷贝到一个新集合中
	 * 注意: 如果source为null则null, 如果array未空数组则返回空集合
	 */
	public static <T> List<T> copyAsList(Object[] source, Class<T> type) {
		if (source == null) {
			return null;
		}
		if (source.length == 0) {
			return Collections.emptyList();
		}
		return MAPPER_FACADE.mapAsList(source, type);
	}

	private BeanUtil() throws IllegalAccessException {
		throw new IllegalAccessException("不允许实例化");
	}
}
