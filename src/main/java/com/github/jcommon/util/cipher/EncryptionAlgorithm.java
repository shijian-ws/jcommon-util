package com.github.jcommon.util.cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

/**
 * 加密算法
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public interface EncryptionAlgorithm {
    /**
     * 密钥服务提供商BouncyCastle，可以使用Security.addProvider(PROVIDER);添加到默认，也可以再每个构造器中指定
     */
    Provider PROVIDER = new BouncyCastleProvider();

    int PROVIDER_INDEX = Security.addProvider(PROVIDER);

    /**
     * 密钥算法
     */
    String getAlgorithmName();

    /**
     * 密钥强度
     */
    int getStrength();
}
