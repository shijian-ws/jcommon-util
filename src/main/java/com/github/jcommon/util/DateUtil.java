package com.github.jcommon.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期与时间工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2021-01-30
 */
public final class DateUtil {
    private static final Map<String, DateTimeFormatter> FORMAT_CACHE = new ConcurrentHashMap<>();

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";

    /**
     * 获取当前时区时间戳
     */
    public static long getCurrentMillis() {
        return Clock.system(getDefaultZoneId()).millis();
    }

    /**
     * 获取世界协调时时间戳，无时区
     */
    public static long getCurrentUTCMillis() {
        return Clock.systemUTC().millis();
    }

    /**
     * 获取当前日期的格式化字符串，按照"yyyy-MM-dd"格式化
     */
    public static String formatDate() {
        return formatDateTime(YYYY_MM_DD, zonedDateTimeNow());
    }

    /**
     * 将指定日期时间按照"yyyy-MM-dd"格式化为字符串
     */
    public static String formatDate(Date date) {
        return formatDateTime(YYYY_MM_DD, (TemporalAccessor) toLocalDateTime(date));
    }

    /**
     * 将指定日期时间按照"yyyy-MM-dd"格式化为字符串
     */
    public static String formatDate(LocalDate date) {
        return formatDateTime(YYYY_MM_DD, (TemporalAccessor) date.atStartOfDay());
    }

    /**
     * 将指定时间戳按照"yyyy-MM-dd"格式化为字符串
     */
    public static String formatDate(long date) {
        return formatDateTime(YYYY_MM_DD, (TemporalAccessor) toLocalDateTime(date));
    }

    /**
     * 获取当前时间的格式化字符串，按照"HH:mm:ss"格式化
     */
    public static String formatTime() {
        return formatDateTime(HH_MM_SS, zonedDateTimeNow());
    }

    /**
     * 将当前时间按照"HH:mm:ss"格式化为字符串
     */
    public static String formatTime(Date time) {
        return formatDateTime(HH_MM_SS, toLocalTime(time));
    }

    /**
     * 将当前时间按照"HH:mm:ss"格式化为字符串
     */
    public static String formatTime(LocalTime time) {
        return formatDateTime(HH_MM_SS, time);
    }

    /**
     * 将当前时间戳按照"HH:mm:ss"格式化为字符串
     */
    public static String formatTime(long time) {
        return formatDateTime(HH_MM_SS, (TemporalAccessor) toLocalDateTime(time));
    }

    /**
     * 将当前日期时间按照"yyyy-MM-dd HH:mm:ss"格式转换为字符串
     */
    public static String formatDateTime() {
        return formatDateTime(YYYY_MM_DD_HH_MM_SS, zonedDateTimeNow());
    }

    /**
     * 将当前时间按照自定义格式转换为字符串
     */
    public static String formatDateTime(String pattern) {
        return formatDateTime(pattern, zonedDateTimeNow());
    }

    /**
     * 将指定日期时间按照"yyyy-MM-dd HH:mm:ss"格式转换为字符串
     */
    public static String formatDateTime(Date dateTime) {
        return formatDateTime(YYYY_MM_DD_HH_MM_SS, (TemporalAccessor) toLocalDateTime(dateTime));
    }

    /**
     * 将指定日期时间按照"yyyy-MM-dd HH:mm:ss"格式转换为字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(YYYY_MM_DD_HH_MM_SS, (TemporalAccessor) dateTime);
    }

    /**
     * 将指定日期时间按照"yyyy-MM-dd HH:mm:ss"格式转换为字符串
     */
    public static String formatDateTime(long dateTime) {
        return formatDateTime(YYYY_MM_DD_HH_MM_SS, (TemporalAccessor) toLocalDateTime(dateTime));
    }

    /**
     * 将指定日期时间按照指定格式转换为字符串
     */
    public static String formatDateTime(String pattern, Date dateTime) {
        return formatDateTime(pattern, (TemporalAccessor) toLocalDateTime(dateTime));
    }

    /**
     * 将指定日期时间按照指定格式转换为字符串
     */
    public static String formatDateTime(String pattern, LocalDate date) {
        return formatDateTime(pattern, (TemporalAccessor) date.atStartOfDay());
    }

    /**
     * 将指定日期时间按照指定格式转换为字符串
     */
    public static String formatDateTime(String pattern, LocalDateTime dateTime) {
        return formatDateTime(pattern, (TemporalAccessor) dateTime);
    }

    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMAT_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * 获取当前默认时区
     */
    public static ZoneId getDefaultZoneId() {
        return ZoneId.systemDefault();
    }

    /**
     * 获取当前默认时区
     */
    public static TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }

    /**
     * 获取当前时间点的ZonedDateTime
     */
    private static ZonedDateTime zonedDateTimeNow() {
        return ZonedDateTime.now(getDefaultZoneId());
    }

    /**
     * 获取当前时间点的LocalDate
     */
    private static LocalDate localDateNow() {
        return LocalDate.now(getDefaultZoneId());
    }

    /**
     * 获取当前时间点的LocalDateTime
     */
    private static LocalDateTime localDateTimeNow() {
        return LocalDateTime.ofInstant(Instant.now(), getDefaultZoneId());
    }

    /**
     * 将指定日期时间按照指定格式转换为字符串
     */
    private static String formatDateTime(String pattern, TemporalAccessor accessor) {
        return getFormatter(pattern).format(accessor);
    }

    /**
     * 将指定日期时间按照指定格式转换为字符串
     */
    public static String formatDateTime(String pattern, long dateTime) {
        return getFormatter(pattern).format(toLocalDateTime(dateTime));
    }

    /**
     * 将指定日期时间字符串按照自定义格式转换为Date
     */
    public static Date parseDate(String pattern, String dateTime) {
        return toDate(parseLocalDate(pattern, dateTime));
    }

    /**
     * 将指定日期时间字符串按照自定义格式转换为Date
     */
    public static Date parseDateTime(String pattern, String dateTime) {
        return toDate(parseLocalDateTime(pattern, dateTime));
    }

    /**
     * 将指定日期时间字符串按照自定义格式转换为LocalDate对象
     */
    public static LocalDate parseLocalDate(String pattern, String dateTime) {
        return LocalDate.parse(dateTime, getFormatter(pattern));
    }

    /**
     * 将指定日期时间字符串按照自定义格式转换为LocalDateTime对象
     */
    public static LocalDateTime parseLocalDateTime(String pattern, String dateTime) {
        return LocalDateTime.parse(dateTime, getFormatter(pattern));
    }

    /**
     * 将指定时间转换为Date对象
     */
    public static Date getDateTime(long dateTime) {
        return new Date(dateTime);
    }

    /**
     * 将指定时间转换为LocalDateTime对象
     */
    public static LocalDateTime getLocalDateTime(long dateTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), getDefaultZoneId());
    }

    /**
     * 将LocalDate转换为指定时区的Date
     */
    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(getDefaultZoneId()).toInstant());
    }

    /**
     * 将LocalDateTime转换为指定时区的Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(getDefaultZoneId()).toInstant());
    }

    /**
     * 将Date转换为指定时区的LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * 将Date转换为指定时区的LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }

    /**
     * 将Date转换为指定时区的LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), getDefaultZoneId());
    }

    /**
     * 将时间戳转换为指定时区的LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long dateTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), getDefaultZoneId());
    }

    /**
     * 判断指定nanoTime与当前nanoTime相差的时间，用System.nanoTime()的时间
     */
    public static Duration diffNano(long start) {
        return Duration.ofNanos(System.nanoTime() - start);
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(long start) {
        return Duration.between(toLocalDateTime(start), localDateTimeNow());
    }

    /**
     * 判断当前时间与指定时间之间相差的时间
     */
    public static Duration diff(Date start) {
        return Duration.between(toLocalDateTime(start), localDateTimeNow());
    }

    /**
     * 判断当前时间与指定时间之间相差的时间
     */
    public static Duration diff(LocalDateTime start) {
        return Duration.between(start, localDateTimeNow());
    }

    /**
     * 判断当前时间与指定时间之间相差的时间
     */
    public static Duration diff(LocalDate start) {
        return Duration.between(start.atStartOfDay(), localDateNow().atStartOfDay());
    }

    /**
     * 判断当前时间与指定时间之间相差的时间
     */
    public static Duration diff(LocalTime start) {
        return Duration.between(start, LocalTime.now());
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(long end, long start) {
        return Duration.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(Date end, Date start) {
        return Duration.between(toLocalDateTime(start), toLocalDateTime(end));
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(LocalDate end, LocalDate start) {
        return Duration.between(start.atStartOfDay(), end.atStartOfDay());
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(LocalTime end, LocalTime start) {
        return Duration.between(start, end);
    }

    /**
     * 判断指定时间之间相差的时间
     */
    public static Duration diff(LocalDateTime end, LocalDateTime start) {
        return Duration.between(start, end);
    }

    /**
     * 增加月
     */
    public static Date addMonths(Date date, int value) {
        return add(date, Calendar.MONTH, value);
    }

    /**
     * 增加周
     */
    public static Date addWeeks(Date date, int value) {
        return add(date, Calendar.WEEK_OF_YEAR, value);
    }

    /**
     * 增加天
     */
    public static Date addDays(Date date, int value) {
        return add(date, Calendar.DAY_OF_MONTH, value);
    }

    /**
     * 增加时
     */
    public static Date addHours(Date date, int value) {
        return add(date, Calendar.HOUR_OF_DAY, value);
    }

    /**
     * 增加分
     */
    public static Date addMinutes(Date date, int value) {
        return add(date, Calendar.MINUTE, value);
    }

    /**
     * 增加秒
     */
    public static Date addSeconds(Date date, int value) {
        return add(date, Calendar.SECOND, value);
    }

    private static Date add(Date date, int field, int value) {
        if (date == null || value == 0) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, value);
        return c.getTime();
    }

    private DateUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
