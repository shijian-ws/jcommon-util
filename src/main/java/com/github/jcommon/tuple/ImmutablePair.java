package com.github.jcommon.tuple;

/**
 * 不可变的Pair
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class ImmutablePair<L, R> extends BasePair<L, R> implements Pair<L, R> {
    private static final long serialVersionUID = 3262171323605563362L;

    private ImmutablePair(L left, R right) {
        super(left, right);
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }

    public static <L, R> ImmutablePair<L, R> of(L left, R right) {
        return new ImmutablePair<>(left, right);
    }
}
