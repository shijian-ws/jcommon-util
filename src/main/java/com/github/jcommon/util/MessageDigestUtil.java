package com.github.jcommon.util;

import com.github.jcommon.constant.CommonConstant;
import com.github.jcommon.util.cipher.EncryptionAlgorithm;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息摘要工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2016-04-05
 */
public final class MessageDigestUtil {
    enum MessageDigestAlgorithm {
        /**
         * MD5加密摘要对象
         */
        MD5("MD5"),
        /**
         * SHA1加密摘要对象
         */
        SHA1("SHA-1"),
        /**
         * SHA512加密摘要对象
         */
        SHA512("SHA-512");

        private final MessageDigest messageDigest;
        private final String algorithmName;
        private final AtomicBoolean mutx = new AtomicBoolean(false);

        MessageDigestAlgorithm(String algorithmName) {
            this.messageDigest = getInstance(algorithmName);
            this.algorithmName = algorithmName;
        }

        private static MessageDigest getInstance(String algorithmName) {
            try {
                return MessageDigest.getInstance(algorithmName, EncryptionAlgorithm.PROVIDER);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        /**
         * 摘要加密
         */
        public String digest(byte[] data) {
            Objects.requireNonNull(data, "加密数据对象不能为null!");
            if (mutx.compareAndSet(false, true)) {
                // 加锁
                try {
                    return StringUtil.toHex(this.messageDigest.digest(data));
                } finally {
                    this.messageDigest.reset();
                    mutx.set(false);
                }
            }
            return StringUtil.toHex(getInstance(this.algorithmName).digest(data));
        }

        /**
         * 摘要加密
         *
         * @param data
         * @param slat 加盐值
         * @return
         */
        public String digest(byte[] data, String slat) {
            Objects.requireNonNull(data, "加密数据对象不能为null!");
            Objects.requireNonNull(slat, "加盐数据对象不能为null!");
            int hashCode = slat.hashCode();
            StringBuilder buf = new StringBuilder(data.length << 1);
            for (int x = 0, y = data.length; x < y; x++) {
                // 加盐
                buf.append(data[x]).append(Integer.toHexString(hashCode >>> x & 0xF));
            }
            return this.digest(buf.toString().getBytes(CommonConstant.UTF8_CHARSET));
        }
    }

    /**
     * 摘要加密
     *
     * @param algorithm 摘要算法对象
     * @param value     加密字符串
     * @return
     */
    private static String md(MessageDigestAlgorithm algorithm, String value) {
        Objects.requireNonNull(value, "加密数据不能为null!");
        Objects.requireNonNull(algorithm, "摘要算法对象不能为null!");
        return algorithm.digest(value.getBytes(CommonConstant.UTF8_CHARSET));
    }

    /**
     * 摘要加密
     *
     * @param algorithm 摘要算法对象
     * @param data      加密字符串
     * @return
     */
    private static String md(MessageDigestAlgorithm algorithm, byte[] data) {
        Objects.requireNonNull(data, "加密数据不能为null!");
        Objects.requireNonNull(algorithm, "摘要算法对象不能为null!");
        return algorithm.digest(data);
    }

    /**
     * 将一个字符串进行MD5加密，并返回小写的加密32位字符串
     */
    public static String md5(String value) {
        return md5(value, 32);
    }

    /**
     * 将一个字符串进行MD5加密，并返回小写的加密32位字符串
     */
    public static String md5(byte[] data) {
        return md5(data, 32);
    }

    /**
     * 将一个字符串进行MD5加密，并返回小写的加密16位或32位字符串
     */
    public static String md5(String value, int len) {
        String message = md(MessageDigestAlgorithm.MD5, value);
        if (len == 16) {
            return message.substring(8, 24);
        }
        return message;
    }

    /**
     * 将一个字符串进行MD5加密，并返回小写的加密16位或32位字符串
     */
    public static String md5(byte[] data, int len) {
        String message = md(MessageDigestAlgorithm.MD5, data);
        if (len == 16) {
            return message.substring(8, 24);
        }
        return message;
    }

    /**
     * 将一个字符数组进行SHA1加密，并返回小写的加密40位字符串
     */
    public static String sha1(String value) {
        return md(MessageDigestAlgorithm.SHA1, value);
    }

    /**
     * 将一个字符数组进行SHA1加密，并返回小写的加密40位字符串
     */
    public static String sha1(byte[] data) {
        return md(MessageDigestAlgorithm.SHA1, data);
    }

    /**
     * 将一个字符数组进行SHA512加密，并返回小写的加密128位字符串
     */
    public static String sha512(String value) {
        return md(MessageDigestAlgorithm.SHA512, value);
    }

    /**
     * 将一个字符数组进行SHA512加密，并返回小写的加密128位字符串
     */
    public static String sha512(byte[] data) {
        return md(MessageDigestAlgorithm.SHA512, data);
    }

    private MessageDigestUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
