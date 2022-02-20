package com.github.jcommon.util.cipher;

import com.github.jcommon.constant.CommonConstant;
import com.github.jcommon.util.Base64Util;
import com.github.jcommon.util.StringUtil;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Objects;

/**
 * 非对称加密算法
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public enum AsymmetricAlgorithm implements EncryptionAlgorithm {
    /**
     * 非对称加密算法
     */
    RSA("RSA", 1024);

    /**
     * 算法名称
     */
    private final String algorithmName;
    private final String signatureAlgorithmName;
    /**
     * 秘钥强度
     */
    private final int strength;
    /**
     * 秘钥生成器
     */
    private final KeyPairGenerator keyPairGenerator;
    private static final JcaPEMKeyConverter KEY_CONVERTER = new JcaPEMKeyConverter().setProvider(PROVIDER);

    AsymmetricAlgorithm(String algorithmName, int strength) {
        this.algorithmName = algorithmName;
        this.signatureAlgorithmName = algorithmName;
        this.strength = strength;
        try {
            this.keyPairGenerator = KeyPairGenerator.getInstance(algorithmName, PROVIDER);
            this.keyPairGenerator.initialize(strength);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
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
     * 加密操作
     *
     * @param key
     * @param data
     * @return
     */
    private String byEncrypt(Key key, String data) {
        Objects.requireNonNull(key, "key不能为空!");
        Objects.requireNonNull(data, "data不能为空!");
        try {
            byte[] result = crypt(true, key, data.getBytes(CommonConstant.UTF8_CHARSET));
            return Base64Util.encodeAsString(result);
        } catch (Exception e) {
            throw new RuntimeException("加密失败: ", e);
        }
    }

    /**
     * 解密操作
     *
     * @param key
     * @param data
     * @return
     */
    private String byDecrypt(Key key, String data) {
        Objects.requireNonNull(key, "key不能为空!");
        Objects.requireNonNull(data, "data不能为空!");
        byte[] bytes = Base64Util.decodeAsByteArray(data);
        try {
            byte[] result = crypt(false, key, bytes);
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException("解密失败: ", e);
        }
    }

    /**
     * 加密解密操作
     *
     * @param isEncrypt 是否加密
     * @param key       密钥
     * @param data      数据
     * @return
     */
    private byte[] crypt(boolean isEncrypt, Key key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, ShortBufferException {
        Objects.requireNonNull(key, "key不能为空!");
        Objects.requireNonNull(data, "data不能为空!");
        Cipher cipher = Cipher.getInstance(this.algorithmName, PROVIDER);
        cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param key
     * @param password
     * @param data
     * @return
     */
    public String encrypt(String key, String password, String data) {
        Objects.requireNonNull(key, "key不能为空!");
        return byEncrypt(parseKeyPair(key, password).getPrivate(), data);
    }

    /**
     * 公钥加密
     *
     * @param publicKey
     * @param data
     * @return
     */
    public String encryptByPublic(String publicKey, String data) {
        Objects.requireNonNull(publicKey, "publicKey不能为空!");
        return byEncrypt(parsePublicKey(publicKey), data);
    }

    /**
     * 私钥解密
     *
     * @param privateKey
     * @param password
     * @param data
     * @return
     */
    public String decrypt(String privateKey, String password, String data) {
        Objects.requireNonNull(privateKey, "privateKey不能为空!");
        return byDecrypt(parseKeyPair(privateKey, password).getPrivate(), data);
    }

    /**
     * 公钥解密
     *
     * @param publicKey
     * @param data
     * @return
     */
    public String decryptByPublic(String publicKey, String data) {
        Objects.requireNonNull(publicKey, "publicKey不能为空!");
        return byDecrypt(parsePublicKey(publicKey), data);
    }

    /**
     * 签名
     */
    public String sign(String privateKey, String password, String data) {
        try {
            Signature signature = Signature.getInstance(this.algorithmName, PROVIDER);
            signature.initSign(parseKeyPair(privateKey, password).getPrivate());
            signature.update(data.getBytes(CommonConstant.UTF8_CHARSET));
            return new String(signature.sign());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 验签
     */
    public boolean verify(String publicKey, String sign, String data) {
        try {
            Signature signature = Signature.getInstance(this.algorithmName, PROVIDER);
            signature.initVerify(parsePublicKey(publicKey));
            signature.update(data.getBytes(CommonConstant.UTF8_CHARSET));
            return signature.verify(sign.getBytes(CommonConstant.UTF8_CHARSET));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * 生成未加密的私钥
     */
    public String generatePrivateKey() {
        return generatePrivateKey(null);
    }

    /**
     * 生成加密的私钥
     */
    public String generatePrivateKey(String password) {
        try {
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            return convert(privateKey, password);
        } catch (Exception e) {
            throw new RuntimeException("生成私钥失败: ", e);
        }
    }

    /**
     * 将密钥信息转换为字符串，可对私钥进行加密
     */
    private String convert(Object key, String password) {
        Objects.requireNonNull(key, "密钥对象不能为空!");
        try (StringWriter buf = new StringWriter()) {
            try (JcaPEMWriter writer = new JcaPEMWriter(buf)) {
                Object obj = key;
                if (StringUtil.isNotBlank(password) && key instanceof PrivateKey) {
                    // 需要使用JceOpenSSLPKCS8DecryptorProviderBuilder解密
                    OutputEncryptor encryptor = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES)
                            .setProvider(PROVIDER).setPasssword(password.toCharArray()).build();
                    obj = new JcaPKCS8Generator((PrivateKey) key, encryptor).generate();
                    // 需要JcePEMDecryptorProviderBuilder解密
                    // obj = new JcePEMEncryptorBuilder("DES-EDE3-CBC").setProvider(PROVIDER).builder(password.toCharArray());
                }
                writer.writeObject(obj);
            } catch (Exception e) {
                throw new RuntimeException("转换密钥失败: ", e);
            }
            return buf.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 生成公钥
     */
    public String generatePublicKey(String privateKey, String password) {
        try {
            KeyPair keyPair = parseKeyPair(privateKey, password);
            return convert(keyPair.getPublic(), null);
        } catch (Exception e) {
            throw new RuntimeException("生成公钥失败: ", e);
        }
    }

    /**
     * 将私钥字符串转换为秘钥对
     */
    private KeyPair parseKeyPair(String privateKey, String password) {
        Object obj = parseKey(new StringReader(privateKey));
        Objects.requireNonNull(obj, "没有加载到私钥信息!");
        try {
            if (obj instanceof PEMKeyPair) {
                // 解析成功
                PEMKeyPair keyPair = (PEMKeyPair) obj;
                return KEY_CONVERTER.getKeyPair(keyPair);
            } else if (obj instanceof PKCS8EncryptedPrivateKeyInfo) {
                // 解析到了带有密码的私钥
                if (StringUtil.isBlank(password)) {
                    throw new RuntimeException("解析到加密的私钥，需要密码!");
                }
                PKCS8EncryptedPrivateKeyInfo encryptInfo = (PKCS8EncryptedPrivateKeyInfo) obj;
                PrivateKeyInfo keyInfo;
                try {
                    // 使用密码解密，注意密码解析器的类型，需使用OpenSSL生成的加密密钥
                    keyInfo = encryptInfo.decryptPrivateKeyInfo(new JceOpenSSLPKCS8DecryptorProviderBuilder()
                            .setProvider(PROVIDER).build(password.toCharArray()));
                } catch (Exception e) {
                    throw new RuntimeException("解密私钥时发生错误: ", e);
                }
                // 针对已解密的密钥进行重新加载
                Object key = parseKey(new StringReader(convert(KEY_CONVERTER.getPrivateKey(keyInfo), null)));
                if (key instanceof PEMKeyPair) {
                    // 解析成功
                    PEMKeyPair keyPair = (PEMKeyPair) key;
                    return KEY_CONVERTER.getKeyPair(keyPair);
                }
                throw new RuntimeException("解析错误，生成公钥的私钥是未知私钥类型: " + obj.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        throw new RuntimeException("未知私钥类型: " + obj.getClass());
    }

    /**
     * 将公钥字符串转换为公钥对象
     */
    public static PublicKey parsePublicKey(String publicKey) {
        try {
            Object obj = parseKey(new StringReader(publicKey));
            Objects.requireNonNull(obj, "没有加载到公钥信息!");
            if (obj instanceof SubjectPublicKeyInfo) {
                // 解析成功
                SubjectPublicKeyInfo info = (SubjectPublicKeyInfo) obj;
                return KEY_CONVERTER.getPublicKey(info);
            }
            throw new RuntimeException("未知公钥类型: " + obj.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解析字符流中的密钥信息
     */
    private static Object parseKey(Reader reader) {
        try (PEMParser parser = new PEMParser(reader)) {
            return parser.readObject();
        } catch (Exception e) {
            throw new RuntimeException("解析密钥失败: ", e);
        }
    }
}
