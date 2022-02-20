package com.github.jcommon.tuple;

import java.io.Serializable;
import java.util.Map;

/**
 * 存储两个元素
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public interface Pair<L, R> extends /*Comparable<Pair<L, R>>, */Map.Entry<L, R>, Serializable {
    /**
     * 获取左值
     */
    L getLeft();

    /**
     * 获取右值
     */
    R getRight();

    @Override
    default L getKey() {
        return this.getLeft();
    }

    @Override
    default R getValue() {
        return this.getRight();
    }

    /*@Override
    default int compareTo(Pair<L, R> o) {
        if (this == o) {
            return 0;
        }
        int cmp = CompareUtil.compare(this.getLeft(), o.getLeft());
        if (cmp == 0) {
            cmp = CompareUtil.compare(this.getRight(), o.getRight());
        }
        return cmp;
    }*/
}
