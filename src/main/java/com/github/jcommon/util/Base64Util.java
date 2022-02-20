package com.github.jcommon.util;

import com.github.jcommon.constant.CommonConstant;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Base64工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2016-04-05
 */
public final class Base64Util {
    /**
     * 对字节数组进行Base64加密
     */
    public static byte[] encode(byte[] bs) {
        return encode(bs, Base64.getEncoder());
    }

    /**
     * 对字节数组进行Base64加密
     */
    public static String encodeAsString(byte[] bs) {
        return encodeAsString(bs, Base64.getEncoder());
    }

    /**
     * 对字符串进行Base64加密
     */
    public static String encode(String value) {
        return encode(value, Base64.getEncoder());
    }

    /**
     * 对字符串进行Base64加密
     */
    public static byte[] encodeAsByteArray(String value) {
        return encode(value.getBytes(CommonConstant.UTF8_CHARSET), Base64.getEncoder());
    }

    /**
     * 对字节数组进行Url格式的Base64加密
     */
    public static String urlEncode(byte[] bs) {
        return new String(encode(bs, Base64.getUrlEncoder()));
    }

    /**
     * 对字符串进行Url格式的Base64加密
     */
    public static String urlEncode(String value) {
        return encode(value, Base64.getUrlEncoder());
    }

    /**
     * 对Base64格式字符串进行解密
     */
    public static String decode(String value) {
        return decodeAsString(value.getBytes(CommonConstant.UTF8_CHARSET), Base64.getDecoder());
    }

    /**
     * 对Base64格式字符串进行解密
     */
    public static byte[] decodeAsByteArray(String value) {
        return decode(value.getBytes(CommonConstant.UTF8_CHARSET), Base64.getDecoder());
    }

    /**
     * 对Base64格式字节数组进行解密
     */
    public static byte[] decode(byte[] bs) {
        return decode(bs, Base64.getDecoder());
    }

    /**
     * 对Base64格式字节数组进行解密
     */
    public static String decodeAsString(byte[] bs) {
        return decodeAsString(bs, Base64.getDecoder());
    }

    /**
     * 对Base64格式字符串进行解密
     */
    public static String urlDecode(String value) {
        return decode(value, Base64.getUrlDecoder());
    }

    /**
     * 对Base64格式字符串进行解密
     */
    public static byte[] urlDecodeAsByteArray(String value) {
        return decodeByByteArray(value.getBytes(CommonConstant.UTF8_CHARSET), Base64.getUrlDecoder());
    }

    private static String encode(String value, Encoder encoder) {
        return encodeAsString(value.getBytes(CommonConstant.UTF8_CHARSET), encoder);
    }

    private static String encodeAsString(byte[] bs, Encoder encoder) {
        byte[] encode = encoder.encode(bs);
        return new String(encode);
    }

    private static byte[] encode(byte[] bs, Encoder encoder) {
        return encoder.encode(bs);
    }

    private static String decode(String value, Decoder decoder) {
        return decodeAsString(value.getBytes(CommonConstant.UTF8_CHARSET), decoder);
    }

    private static String decodeAsString(byte[] bs, Decoder decoder) {
        byte[] decode = decoder.decode(bs);
        return new String(decode);
    }

    private static byte[] decode(byte[] bs, Decoder decoder) {
        return decoder.decode(bs);
    }

    private static byte[] decodeByByteArray(byte[] bs, Decoder decoder) {
        return decoder.decode(bs);
    }

    private Base64Util() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
