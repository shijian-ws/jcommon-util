package com.github.jcommon.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-01-23
 */
public class RandomUtil {
    private static final SecureRandom DEFAULT_SECURE_RANDOM = new SecureRandom();
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static SecureRandom getDefaultSecureRandom() {
        return DEFAULT_SECURE_RANDOM;
    }

    /**
     * 获取一个随机数
     */
    public static int nextInt() {
        return RANDOM.nextInt();
    }

    /**
     * 获取从0到bound-1的随机数
     */
    public static int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * 获取从origin到bound-1的随机数
     */
    public static int nextInt(int origin, int bound) {
        return RANDOM.nextInt(origin, bound);
    }

    /**
     * 获取一个随机数
     */
    public static long nextLong() {
        return RANDOM.nextLong();
    }

    /**
     * 获取从0到bound-1的随机数
     */
    public static long nextLong(long bound) {
        return RANDOM.nextLong(bound);
    }

    /**
     * 获取从origin到bound-1的随机数
     */
    public static long nextLong(long origin, long bound) {
        return RANDOM.nextLong(origin, bound);
    }

    private RandomUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
