package com.github.jcommon.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip工具
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2016-07-01
 */
public final class ZipUtil {

    /**
     * 解压zip压缩数据
     */
    public static byte[] decompress(byte[] data) {
        if (data != null && data.length > 0) {
            Inflater decompress = new Inflater();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                // 需要解压的数据
                decompress.setInput(data);
                byte[] buf = new byte[1024];
                do {
                    // 未读取完成则循环写入内存
                    int len = decompress.inflate(buf);
                    if (len == 0) {
                        // TODO 解压数据不完整
                        break;
                    }
                    os.write(buf, 0, len);
                } while (!decompress.finished());
                return os.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("解压zip数据失败!", e);
            } finally {
                decompress.end();
            }
        }
        return data;
    }

    /**
     * zip压缩数据
     */
    public static byte[] compress(byte[] data) {
        if (data != null && data.length > 0) {
            Deflater compress = new Deflater();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                // 需要压缩的数据
                compress.setInput(data);
                // 压缩到缓冲区结尾
                compress.finish();
                byte[] buf = new byte[1024];
                do {
                    // 未读取完成则循环写入内存
                    int len = compress.deflate(buf);
                    os.write(buf, 0, len);
                } while (!compress.finished());
                return os.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("压缩数据失败!", e);
            } finally {
                compress.end();
            }
        }
        return data;
    }

    /**
     * 创建zip并添加条目
     */
    public static byte[] generateZip(Map<String, byte[]> entries) {
        return addZipEntry(null, entries);
    }

    /**
     * 给zip压缩包添加条目
     *
     * @param zip
     * @param entries
     * @return
     */
    public static byte[] addZipEntry(byte[] zip, Map<String, byte[]> entries) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            if (zip != null && zip.length > 0) {
                os.write(zip);
            }
            ZipOutputStream zos = new ZipOutputStream(os);
            if (entries != null && !entries.isEmpty()) {
                for (Entry<String, byte[]> en : entries.entrySet()) {
                    String name = en.getKey();
                    byte[] data = en.getValue();
                    try {
                        ZipEntry entry = new ZipEntry(name);
                        zos.putNextEntry(entry);
                        // 写入数据
                        zos.write(data);
                        // 关闭当前条目，指向下一个条目
                        zos.closeEntry();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("压缩zip包失败!", e);
        }
    }

    public static String getZipContent(byte[] zipData, String entryName) {
        if (entryName != null && !"".equals(entryName = entryName.trim())) {
            try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(zipData))) {
                for (ZipEntry en; (en = zip.getNextEntry()) != null; ) {
                    if (entryName.equalsIgnoreCase(en.getName())) {
                        if (!en.isDirectory()) {
                            byte[] buffer = new byte[2048];
                            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                                for (int len; (len = zip.read(buffer)) != -1; ) {
                                    os.write(buffer, 0, len);
                                }
                                return new String(os.toByteArray());
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    }
                    zip.closeEntry();
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private ZipUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
