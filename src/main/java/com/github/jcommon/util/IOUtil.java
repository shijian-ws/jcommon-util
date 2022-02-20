package com.github.jcommon.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-01-16
 */
public final class IOUtil {
	public static final int BUFFER_SIZE = 8192;
	public static final byte[] EMPTY_BYTE_ARRAY = {};
	public static final InputStream EMPTY_INPUT_STREAM = new InputStream() {
		@Override
		public int read() {
			return -1;
		}
	};

	/**
	 * 读取字节流中数据并返回
	 */
	public static byte[] copyToByteArray(InputStream is) {
		if (is == null) {
			return EMPTY_BYTE_ARRAY;
		}
		return copy(is, new ByteArrayOutputStream()).toByteArray();
	}

	/**
	 * 将输入字节流写入输出字节流中, 并返回输出字节流对象
	 */
	public static <T extends OutputStream> T copy(InputStream is, T os) {
		if (is != null) {
			byte[] buf = new byte[BUFFER_SIZE];
			try {
				for (int len; (len = is.read(buf)) != -1; ) {
					os.write(buf, 0, len);
				}
				os.flush();
			} catch (Throwable e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		return os;
	}

	private IOUtil() throws IllegalAccessException {
		throw new IllegalAccessException("不允许实例化");
	}
}
