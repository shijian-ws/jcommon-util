package com.github.jcommon.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UUID生成器
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2020-01-25
 */
public final class UUID {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    public static String generate() {
        return generate(null, null);
    }

    /**
     * 生成uuid, 服务节点需要, 一般可用zk生成有序临时节点
     */
    public static String generate(String appName, String nodeSeq) {
        // 1, 应用名称, 4位
        // 2, 节点序号, 2位
        // 3, 年月日时分秒, 12位
        // 4, 自增值, 4位, 位运算只取到8191
        return String.format("%4s%2s%s%04d",
                StringUtil.isBlank(appName) ? "0000" : appName,
                Optional.ofNullable(nodeSeq).orElse("00"),
                FORMATTER.format(LocalDateTime.now()),
                COUNTER.getAndIncrement() & 0x1FFF).toLowerCase();
    }

    private UUID() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
