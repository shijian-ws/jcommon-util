package com.github.jcommon.constant;

/**
 * 常量
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-02-27
 */
public class SystemConstant {
    /**
     * OS
     */
    public static final String OS_NAME = "os.name";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_VERSION = "os.version";
    /**
     * Java
     */
    public static final String JAVA_NAME = "java.name";
    public static final String JAVA_VENDOR = "java.vendor";
    public static final String JAVA_VERSION = "java.version";

    /**
     * 临时目录
     */
    public static final String JAVA_TEMP_DIR = "java.io.tmpdir";

    private SystemConstant() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
