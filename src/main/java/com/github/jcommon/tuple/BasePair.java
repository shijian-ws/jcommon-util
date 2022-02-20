package com.github.jcommon.tuple;

/**
 * Pair基本实现
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public class BasePair<L, R> implements Pair<L, R> {
    private static final long serialVersionUID = 3262171478605563362L;

    private final L left;
    private R right;

    protected BasePair(L left) {
        this.left = left;
    }

    protected BasePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    protected void setRight(R right) {
        this.right = right;
    }

    @Override
    public R setValue(R value) {
        R right = this.right;
        this.right = value;
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }

        Pair pair = (Pair) o;
        return (getLeft() == null ? pair.getLeft() == null : getLeft().equals(pair.getLeft())) &&
                (getRight() == null ? pair.getRight() == null : getRight().equals(pair.getRight()));
    }

    @Override
    public int hashCode() {
        return (getLeft() == null ? 0 : getLeft().hashCode()) ^ (getRight() == null ? 0 : getRight().hashCode());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "left=" + this.getLeft() +
                ", right=" + this.getRight() +
                '}';
    }

    public static <L, R> BasePair<L, R> of(L left, R right) {
        return new BasePair<>(left, right);
    }
}
