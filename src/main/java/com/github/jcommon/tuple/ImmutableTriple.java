package com.github.jcommon.tuple;

import java.util.Objects;

/**
 * 不可变的Triple
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class ImmutableTriple<L, M, R> implements Triple<L, M, R> {
	private final L left;
	private final M middle;
	private final R right;

	private ImmutableTriple(L left, M middle, R right) {
		this.left = left;
		this.middle = middle;
		this.right = right;
	}

	@Override
	public L getLeft() {
		return left;
	}

	@Override
	public M getMiddle() {
		return middle;
	}

	@Override
	public R getRight() {
		return right;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ImmutableTriple<?, ?, ?> that = (ImmutableTriple<?, ?, ?>) o;
		return Objects.equals(left, that.left) && Objects.equals(middle, that.middle) && Objects.equals(right, that.right);
	}

	@Override
	public int hashCode() {
		return Objects.hash(left, middle, right);
	}

	@Override
	public String toString() {
		return "ImmutableTriple{" +
				"left=" + left +
				", middle=" + middle +
				", right=" + right +
				'}';
	}

	public static <L, M, R> ImmutableTriple<L, M, R> of(L left, M middle, R right) {
		return new ImmutableTriple<>(left, middle, right);
	}
}
