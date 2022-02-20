package com.github.jcommon.iterator;

import com.github.jcommon.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * 可转换迭代器
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-30
 */
public class TransformedIterator<FROM, TO> implements Iterator<TO> {
	private final Iterator<FROM> iterator;
	private final Function<FROM, TO> func;

	private TransformedIterator(Iterator<FROM> iterator, Function<FROM, TO> func) {
		this.iterator = iterator;
		this.func = func;
	}

	@Override
	public boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}

	@Override
	public TO next() {
		if (this.hasNext()) {
			return func.apply(iterator.next());
		}
		throw new NoSuchElementException();
	}

	public static <FROM, TO> Iterator<TO> of(Iterator<FROM> iterator, Function<FROM, TO> func) {
		if (iterator == null) {
			return Collections.emptyIterator();
		}
		Assert.notNull(func, "func must be not null");
		return new TransformedIterator<>(iterator, func);
	}
}
