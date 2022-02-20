package com.github.jcommon.tuple;

import com.github.jcommon.util.Assert;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 懒加载的Pair
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public class LazyPair<L, R> extends BasePair<L, R> implements Pair<L, R> {
    private static final long serialVersionUID = 3262171323505563362L;

    private final Object func;

    private LazyPair(L left, Function<L, R> func) {
        super(left);
        this.func = Assert.notNull(func, "func must be mot null");
    }

    private LazyPair(L left, Supplier<R> func) {
        super(left);
        this.func = Assert.notNull(func, "func must be mot null");
    }

    /**
     * 加载value
     */
    @SuppressWarnings("unchecked")
    private R load() {
        R right = this.getRight();
        if (right == null && func != null) {
            synchronized (this) {
                if ((right = this.getRight()) == null) {
                    if (func instanceof Function) {
                        right = ((Function<L, R>) func).apply(this.getLeft());
                    } else if (func instanceof Supplier) {
                        right = ((Supplier<R>) func).get();
                    }
                    if (right != null) {
                        setRight(right);
                    }
                }
            }
        }
        return right;
    }

    @Override
    public R getRight() {
        return this.load();
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }

    public static <L, R> LazyPair<L, R> of(L left, Function<L, R> func) {
        return new LazyPair<>(left, func);
    }

    public static <L, R> LazyPair<L, R> of(L left, Supplier<R> func) {
        return new LazyPair<>(left, func);
    }
}
