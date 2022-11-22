package com.github.jcommon.util;

import com.github.jcommon.constant.CommonConstant;
import com.github.jcommon.constant.SystemConstant;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * File工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2020-07-04
 */
public final class FileUtil {
	private static final Path TEMP_DIR_PATH;
	private static final OpenOption[] READ_OPTION = {StandardOpenOption.READ};
	private static final OpenOption[] APPEND_OPTION = {StandardOpenOption.APPEND};
	/*private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();
	private static final WatchService WATCH_SERVICE = FILE_SYSTEM.newWatchService();*/

	static {
		String tmpDir = null;
		try {
			URL resource = FileUtil.class.getClassLoader().getResource(CommonConstant.STRING_EMPTY);
			if (resource != null) {
				tmpDir = resource.getFile();
				int index = tmpDir.indexOf("target");
				if (index > -1) {
					// 包含target, 可能是Maven
					tmpDir = tmpDir.substring(0, index + 7);
				}
			}
		} catch (Throwable e) {
		}
		if (tmpDir == null) {
			tmpDir = System.getProperty(SystemConstant.JAVA_TEMP_DIR);
		}
		TEMP_DIR_PATH = toAbsolutePath(Paths.get(tmpDir));
	}

	/**
	 * 获取临时目录
	 */
	public static Path getTempDirectory() {
		return TEMP_DIR_PATH;
	}

	/**
	 * 在临时目录下创建子目录
	 */
	public static Path createTempDirectory(String dirName) {
		Assert.notBlank(dirName, "dirName must be not blank");
		return mkdirs(TEMP_DIR_PATH.resolve(dirName));
	}

	/**
	 * 创建多级目录
	 */
	public static Path mkdirs(String root, String... chilren) {
		return create(Paths.get(root, chilren), true);
	}

	/**
	 * 创建目录
	 */
	public static Path mkdirs(Path path) {
		Assert.notNull(path, "path must be not null");
		return create(path, true);
	}

	/**
	 * 创建操作
	 */
	private static Path create(Path path, boolean createDirectory) {
		Assert.notNull(path, "path must be not null");

		if (Files.exists(path)) {
			if (createDirectory && isDirectory(path)) {
				// 已经存在目录
				return toAbsolutePath(path);
			}
			if (!createDirectory && isRegularFile(path)) {
				// 已经存在文件
				return toAbsolutePath(path);
			}

			if (isOtherFile(path)) {
				throw new IllegalStateException("invalid path: " + path.toString());
			}
			// 存在文件但创建目录或存在目录创建文件需要移除
			if (createDirectory && !isDirectory(path)) {
				delete(path);
			} else if (!createDirectory && !isRegularFile(path)) {
				delete(path);
			}
		} else {
			Path parent = path.getParent();
			if (parent != null) {
				File parentFile = path.getParent().toFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
			}
		}

		try {
			if (createDirectory) {
				Files.createDirectories(path);
			} else {
				Files.createFile(path);
			}
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
		return toAbsolutePath(path);
	}

	/**
	 * 转换为绝对路径
	 */
	private static Path toAbsolutePath(Path path) {
		return path.isAbsolute() ? path : path.toAbsolutePath();
	}

	/**
	 * 移除path
	 */
	public static void delete(Path path) {
		try {
			Files.delete(path);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 移除path
	 */
	public static void deleteIfExists(Path path) {
		if (Files.exists(path)) {
			try {
				Files.deleteIfExists(path);
			} catch (Throwable e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * 在临时目录下创建子目录
	 */
	public static Path createTempFile() {
		return createTempFile(Long.toUnsignedString(RandomUtil.nextLong()), "", "");
	}

	/**
	 * 在临时目录下创建临时文件
	 */
	public static Path createTempFile(String fileName, String suffix) {
		return createTempFile(fileName, "", suffix);
	}

	/**
	 * 在临时目录下创建子目录
	 */
	public static Path createTempFile(String fileName, String prefix, String suffix) {
		Assert.notBlank(fileName, "fileName must be not blank");
		return create(TEMP_DIR_PATH.resolve(Safes.of(prefix) + fileName + Safes.of(suffix)), false);
	}

	/**
	 * 是否存在
	 */
	public static boolean exists(String path) {
		return exists(Paths.get(path));
	}

	/**
	 * 是否存在
	 */
	public static boolean exists(Path path, LinkOption... options) {
		return Files.exists(path, options);
	}

	/**
	 * 是否为目录, 不存在或非目录返回false
	 */
	public static boolean isDirectory(Path path) {
		return Files.isDirectory(path);
	}

	/**
	 * 是否为常规文件, 不存在或非常规文件返回false
	 */
	public static boolean isRegularFile(Path path) {
		return Files.isRegularFile(path);
	}

	/**
	 * 是否为非文件, 不存在或非常规文件返回false
	 * 非常规文件可能是: 本地套接字, 物理设备节点, 管道等
	 */
	public static boolean isOtherFile(Path path) {
		try {
			return Files.readAttributes(path, BasicFileAttributes.class).isOther();
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 打开一个读取流
	 */
	public static InputStream openInputStream(Path path) {
		try {
			return Files.newInputStream(path, READ_OPTION);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 以UTF-8字符集读取文件
	 */
	public static byte[] readAllBytes(Path path) {
		return readAllBytes(path, CommonConstant.UTF8_CHARSET);
	}

	/**
	 * 读取文件
	 */
	public static byte[] readAllBytes(Path path, Charset charset) {
		try {
			return Files.readAllBytes(path);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 以UTF-8字符集读取文件每行内容, 懒加载
	 */
	public static Stream<String> lines(Path path) {
		return lines(path, CommonConstant.UTF8_CHARSET);
	}

	/**
	 * 读取文件每行内容, 懒加载
	 */
	public static Stream<String> lines(Path path, Charset charset) {
		try {
			return Files.lines(path, charset);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 打开一个输出流, 默认为覆盖操作
	 */
	public static OutputStream openOutputStream(Path path, boolean append) {
		if (append) {
			return openOutputStream(path, APPEND_OPTION);
		}
		return openOutputStream(path);
	}

	/**
	 * 打开一个输出流, 默认为覆盖操作, 追加: {@link StandardOpenOption#APPEND}
	 */
	public static OutputStream openOutputStream(Path path, OpenOption... options) {
		try {
			return Files.newOutputStream(path, options);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将字符串写入文件中
	 */
	public static void write(String value, Path path, OpenOption... options) {
		try (OutputStream os = openOutputStream(path, options)) {
			os.write(value.getBytes(CommonConstant.UTF8_CHARSET));
			os.flush();
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 将is流中数据写入文件中
	 */
	public static void write(InputStream is, Path path, OpenOption... options) {
		try (OutputStream os = openOutputStream(path, options)) {
			IOUtil.copy(is, os);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 获取指定路径子目录集
	 */
	public static Iterable<Path> allSubdirectories(Path path) {
		return iterable(true, path, null, false, null);
	}

	/**
	 * 获取指定路径子目录集
	 */
	public static Iterable<Path> allSubdirectories(Path path, String glob) {
		return iterable(true, path, glob, false, null);
	}

	/**
	 * 获取指定路径子目录集
	 */
	public static Iterable<Path> allSubdirectories(Path path, Predicate<Path> filter) {
		return iterable(true, path, null, false, filter);
	}

	/**
	 * 获取指定路径中常规文件集
	 */
	public static Iterable<Path> allSubRegularFiles(Path path) {
		return iterable(false, path, null, false, FileUtil::isRegularFile);
	}

	/**
	 * 获取指定路径中常规文件集
	 */
	public static Iterable<Path> allSubRegularFiles(Path path, String glob) {
		return iterable(false, path, glob, false, FileUtil::isRegularFile);
	}

	/**
	 * 获取指定路径中常规文件集
	 */
	public static Iterable<Path> allSubRegularFiles(Path path, String glob, boolean relativeGlobMode) {
		return iterable(false, path, glob, relativeGlobMode, FileUtil::isRegularFile);
	}

	/**
	 * 迭代path
	 * @param onlyDirIter 是否只迭代目录
	 * @param path 迭代path
	 * @param glob glob表达式
	 * @param relativeGlobMode, 是否相对路径模式, true则取根路径path与被迭代path的差集作为匹配的起始
	 * @param filter
	 * @return
	 */
	private static Iterable<Path> iterable(boolean onlyDirIter, Path path, String glob, boolean relativeGlobMode, Predicate<Path> filter) {
		// 合并过滤规则
		Predicate<Path> predicate;
		if (glob != null && !Objects.equals(String.valueOf(CommonConstant.ASTERISK), glob)) {
			PathMatcher pathMatcher = path.getFileSystem().getPathMatcher("glob:" + glob);
			if (filter == null) {
				predicate = arg -> pathMatcher.matches(relativeGlobMode ? path.relativize(arg) : arg);
			} else {
				predicate = arg -> pathMatcher.matches(relativeGlobMode ? path.relativize(arg) : arg) && filter.test(arg);
			}
		} else {
			predicate = filter;
		}

		try {
			// 只迭代目录
			if (onlyDirIter) {
				if (predicate != null) {
					return Files.newDirectoryStream(path, predicate::test);
				}
				return Files.newDirectoryStream(path);
			}
		} catch (Throwable e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}

		return () -> {
			// TODO 与只迭代目录相比是否每次获取迭代器中元素都可能有变化?
			try {
				Stream<Path> stream = Files.walk(path, Short.MAX_VALUE);
				if (predicate != null) {
					stream = stream.filter(predicate);
				}
				return stream.iterator();
			} catch (Throwable e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new RuntimeException(e.getMessage(), e);
			}
		};
	}

	private FileUtil() throws IllegalAccessException {
		throw new IllegalAccessException("不允许实例化");
	}
}
