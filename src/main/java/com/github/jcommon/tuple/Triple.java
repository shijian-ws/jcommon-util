package com.github.jcommon.tuple;

import java.io.Serializable;

/**
 * 存储三个元素
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public interface Triple<L, M, R> extends /*Comparable<Triple<L, M, R>>, */Serializable {
    /**
     * 获取左值
     */
    L getLeft();

    /**
     * 中间值
     */
    M getMiddle();

    /**
     * 获取右值
     */
    R getRight();
}
