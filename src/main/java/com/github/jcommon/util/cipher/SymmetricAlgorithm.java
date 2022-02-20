package com.github.jcommon.util.cipher;

import com.github.jcommon.constant.CommonConstant;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.KeyGenerator;
import java.util.Objects;

/**
 * 对称加密散发
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public enum SymmetricAlgorithm implements EncryptionAlgorithm {
    /**
     * 对称加密算法
     */
    AES("AES", 128);

    /**
     * 算法名称
     */
    private final String algorithmName;
    /**
     * 算法强度
     */
    private final int strength;
    /**
     * 秘钥生成器
     */
    private final KeyGenerator keyGenerator;
    /**
     * CBC模式
     */
    private final CBCBlockCipher cbcCipher;
    /**
     * 默认初始向量
     */
    private final byte[] defaultIv;

    SymmetricAlgorithm(String algorithmName, int strength) {
        this.algorithmName = algorithmName;
        this.strength = strength;
        try {
            this.keyGenerator = KeyGenerator.getInstance(algorithmName, PROVIDER);
            this.keyGenerator.init(strength);
        } catch (Exception e) {
            throw new RuntimeException("KeyGenerator init error: ", e);
        }

        // 算法引擎
        BlockCipher engine;
        // org.bouncycastle.jcajce.provider.symmetric.AES
        if ("AES".equalsIgnoreCase(algorithmName)) {
            // 快速AES引擎
            engine = new AESEngine();
        } else {
            throw new RuntimeException("not support " + algorithmName + " algorithm");
        }

        this.cbcCipher = new CBCBlockCipher(engine);
        this.defaultIv = new byte[cbcCipher.getBlockSize()];
    }

    @Override
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * 算法强度
     */
    @Override
    public int getStrength() {
        return strength;
    }

    /**
     * 生成密钥
     */
    public String generateKey() {
        return Hex.toHexString(keyGenerator.generateKey().getEncoded());
    }

    /**
     * 数据操作
     *
     * @param forEncryption 是否加密
     * @param key           密钥
     * @param iv            初始向量
     * @param data          数据
     * @return
     */
    private byte[] operate(boolean forEncryption, byte[] key, byte[] iv, byte[] data) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        Objects.requireNonNull(key, "key不能为空!");
        Objects.requireNonNull(data, "data不能为空!");
        if (iv == null) {
            // 未设置初始向量, 使用默认数组
            iv = defaultIv;
        }
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(this.cbcCipher);
        // 加密或解密初始化
        cipher.init(forEncryption, new ParametersWithIV(new KeyParameter(key), iv));
        // 生成缓冲区
        byte[] buf = new byte[cipher.getOutputSize(data.length)];
        // 处理数据, 写入缓冲区
        int processLen = cipher.processBytes(data, 0, data.length, buf, 0);
        int finalLen = cipher.doFinal(buf, processLen);
        byte[] result = new byte[processLen + finalLen];
        // 将缓冲区数据拷贝到数组
        System.arraycopy(buf, 0, result, 0, result.length);
        return result;
    }

    /**
     * 加密数据
     *
     * @param key
     * @param data
     * @return
     */
    public byte[] encrypt(String key, String data) {
        return encrypt(Hex.decode(key), data.getBytes(CommonConstant.UTF8_CHARSET), null);
    }

    /**
     * 加密数据
     *
     * @param key
     * @param data
     * @return
     */
    public byte[] encrypt(String key, byte[] data) {
        return encrypt(Hex.decode(key), data, null);
    }

    /**
     * 加密数据
     *
     * @param key
     * @param data
     * @return
     */
    public byte[] encrypt(byte[] key, byte[] data) {
        return encrypt(key, data, null);
    }

    /**
     * 加密数据
     *
     * @param data 数据
     * @param key  密钥
     * @param iv   初始向量
     * @return
     */
    public byte[] encrypt(byte[] key, byte[] data, byte[] iv) {
        try {
            return operate(true, key, iv, data);
        } catch (Exception e) {
            throw new RuntimeException("encrypt error: ", e);
        }
    }

    /**
     * 解密数据
     */
    public byte[] decrypt(String key, String data) {
        return decrypt(Hex.decode(key), data.getBytes(CommonConstant.UTF8_CHARSET), null);
    }

    /**
     * 解密数据
     */
    public byte[] decrypt(String key, byte[] data) {
        return decrypt(Hex.decode(key), data, null);
    }

    /**
     * 解密数据
     *
     * @param key
     * @param data
     * @return
     */
    public byte[] decrypt(byte[] key, byte[] data) {
        return decrypt(key, data, null);
    }

    /**
     * 解密数据
     *
     * @param data 数据
     * @param key  密钥
     * @param iv   初始向量
     * @return
     */
    public byte[] decrypt(byte[] key, byte[] data, byte[] iv) {
        try {
            return operate(false, key, iv, data);
        } catch (Exception e) {
            throw new RuntimeException("decrypt error: ", e);
        }
    }
}
