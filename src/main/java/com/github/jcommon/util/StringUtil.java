package com.github.jcommon.util;

import com.github.jcommon.constant.CommonConstant;

import java.util.function.IntPredicate;

/**
 * 字符串工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-09
 */
public final class StringUtil {
    /**
     * 占位符
     */
    private static final String LIMIT = "{}";
    private static final IntPredicate IS_WHITE_SPACE = Character::isWhitespace;
    private static final IntPredicate IS_NOT_WHITE_SPACE = IS_WHITE_SPACE.negate();

    /**
     * 判断字符串为null或空串或全部空白字符
     */
    public static boolean isBlank(CharSequence cs) {
        return cs == null
                || cs.length() == 0
                || cs.chars().allMatch(IS_WHITE_SPACE);
    }

    /**
     * 判断字符串为不null且不空串且非全部空白字符
     */
    public static boolean isNotBlank(CharSequence cs) {
        return cs != null
                && cs.length() > 0
                && cs.chars().anyMatch(IS_NOT_WHITE_SPACE);
    }

    /**
     * 使用占位符{}格式化
     */
    public static String formart(String errorMsg, Object... args) {
        if (errorMsg == null || args == null || args.length == 0 || !errorMsg.contains(LIMIT)) {
            return errorMsg;
        }

        StringBuilder buf = new StringBuilder();
        int offset = 0;
        for (Object arg : args) {
            // 检索
            int pos = errorMsg.indexOf(LIMIT, offset);
            if (pos < 0) {
                // 未找到
                break;
            } else if (pos > 0) {
                // 占位符不在开始, 填充非占位符
                buf.append(errorMsg.substring(offset, pos));
            }
            // 替换占位符
            buf.append(arg);
            // 偏移到当前占位符索引之后
            offset = pos + LIMIT.length();
        }

        if (offset < errorMsg.length()) {
            // 还有剩余
            buf.append(errorMsg.substring(offset));
        }

        return buf.toString();
    }

    /**
     * 将数组转换为16进制小写字符串, 如果参数为null或空数组则返回空串("")
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return CommonConstant.STRING_EMPTY;
        }

        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(Integer.toHexString(b >>> 4 & 0xF)).append(Integer.toHexString(b & 0xF));
        }

        return buf.toString().toLowerCase();
    }

    private StringUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
