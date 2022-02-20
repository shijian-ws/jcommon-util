package com.github.jcommon.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2020-01-25
 */
public final class TimeUtil {
	private static Map<String, DateTimeFormatter> FORMAT_CACHE = new ConcurrentHashMap<>();

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static DateTimeFormatter getFormatter(String pattern) {
		return FORMAT_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
	}

	/**
	 * 按照"yyyy-MM-dd"格式将日期的格式化字符串
	 */
	public static String formatDate(Date data) {
		return data == null ? null : formatLocalDate(toLocalDate(data), DEFAULT_DATE_PATTERN);
	}

	/**
	 * 按照指定格式将日期的格式化字符串
	 */
	public static String formatDate(Date data, String pattern) {
		return data == null || pattern == null ? null : formatLocalDate(toLocalDate(data), pattern);
	}

	/**
	 * 按照"yyyy-MM-dd HH:mm:ss"格式将日期的格式化字符串
	 */
	public static String formatDateTime(Date data) {
		return data == null ? null : formatLocalDateTime(toLocalDateTime(data), DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * 按照指定格式将日期的格式化字符串
	 */
	public static String formatDateTime(Date data, String pattern) {
		return data == null || pattern == null ? null : formatLocalDateTime(toLocalDateTime(data), pattern);
	}

	/**
	 * 按照"yyyy-MM-dd"格式将日期的格式化字符串
	 */
	public static String formatLocalDate(LocalDate data) {
		return data == null ? null : formatLocalDate(data, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 按照指定格式将日期的格式化字符串
	 */
	public static String formatLocalDate(LocalDate data, String pattern) {
		return data == null || pattern == null ? null : getFormatter(pattern).format(data);
	}

	/**
	 * 将指定日期时间按照自定义格式转换为字符串
	 */
	public static String formatLocalDateTime(LocalDateTime data) {
		return data == null ? null : formatLocalDateTime(data, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * 将指定日期时间按照自定义格式转换为字符串
	 */
	public static String formatLocalDateTime(LocalDateTime data, String pattern) {
		return data == null || pattern == null ? null : getFormatter(pattern).format(data);
	}

	/**
	 * 将指定日期时间字符串按照"yyyy-MM-dd HH:mm:ss"格式转换为日期
	 */
	public static Date parseDateTime(String strDate) {
		return strDate == null ? null : parseDateTime(strDate, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * 将指定日期时间字符串按照指定格式转换为日期
	 */
	public static Date parseDateTime(String strDate, String pattern) {
		return strDate == null || pattern == null ? null : toDate(parseLocalDateTime(strDate, DEFAULT_DATE_TIME_PATTERN));
	}

	/**
	 * 将指定日期时间字符串按照"yyyy-MM-dd"格式转换为日期
	 */
	public static LocalDate parseLocalDate(String strDate) {
		return strDate == null ? null : parseLocalDate(strDate, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 将指定日期时间字符串按照指定格式转换为日期
	 */
	public static LocalDate parseLocalDate(String strDate, String pattern) {
		return LocalDate.parse(strDate, getFormatter(pattern));
	}

	/**
	 * 将指定日期时间字符串按照"yyyy-MM-dd HH:mm:ss"格式转换为日期
	 */
	public static LocalDateTime parseLocalDateTime(String strDate) {
		return strDate == null ? null : parseLocalDateTime(strDate, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * 将指定日期时间字符串按照指定格式转换为日期
	 */
	public static LocalDateTime parseLocalDateTime(String strDate, String pattern) {
		return LocalDateTime.parse(strDate, getFormatter(pattern));
	}

	/**
	 * Date转换LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = truncate(date, Calendar.MILLISECOND);
		return LocalDateTime.of(
			LocalDate.ofYearDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR)),
			LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
					(int) TimeUnit.NANOSECONDS.convert(calendar.get(Calendar.MILLISECOND), TimeUnit.MILLISECONDS))
		);
	}

	/**
	 * Date转换LocalDate
	 */
	public static LocalDate toLocalDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = truncate(date, Calendar.DAY_OF_MONTH);
		return LocalDate.of(calendar.get(Calendar.YEAR), convertCalendarMonthToMonth(calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static Date toDate(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(localDate.getYear(), convertMonthToCalendarMonth(localDate.getMonth()), localDate.getDayOfMonth());
		return calendar.getTime();
	}

	public static Date toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(
				localDateTime.getYear(), convertMonthToCalendarMonth(localDateTime.getMonth()), localDateTime.getDayOfMonth(),
				localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond()
		);
		calendar.set(Calendar.MILLISECOND, (int) TimeUnit.MILLISECONDS.convert(localDateTime.getNano(), TimeUnit.NANOSECONDS));
		return calendar.getTime();
	}

	private static int convertMonthToCalendarMonth(Month month) {
		return month == null ? 0 : month.getValue() - 1;
	}

	private static Month convertCalendarMonthToMonth(int value) {
		return Month.of(value < 0 ? 1 : value + 1);
	}

	/**
	 * 获取相差的时间点
	 */
	public static Duration diffDateTime(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return Duration.ZERO;
		}

		long timestamp1 = date1 == null ? 0 : date1.getTime();
		long timestamp2 = date2 == null ? 0 : date2.getTime();
		if (timestamp1 == timestamp2) {
			return Duration.ZERO;
		}
		return Duration.ofMillis(timestamp1 > timestamp2 ? timestamp1 - timestamp2 : timestamp2 - timestamp1);
	}

	/**
	 * 指定时间增加月份, 当amount负数时则为减少
	 */
	public static Date addMonths(Date date, int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	/**
	 * 指定时间增加天数, 当amount负数时则为减少
	 */
	public static Date addDays(Date date, int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 指定时间增加小时, 当amount负数时则为减少
	 */
	public static Date addHours(Date date, int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 指定时间增加分钟, 当amount负数时则为减少
	 */
	public static Date addMinutes(Date date, int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	/**
	 * 指定时间增加秒数, 当amount负数时则为减少
	 */
	public static Date addSeconds(Date date, int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	/**
	 * 指定时间增加毫秒数, 当amount负数时则为减少
	 */
	public static Date addMilliseconds(Date date, int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	/**
	 * 按照类型增加时间, 当amount负数时则为减少
	 */
	private static Date add(Date date, int calendarField, int amount) {
		if (date == null || amount == 0) {
			return date;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	/**
	 * 日期比较
	 *
	 * @param date1
	 * @param date2
	 * @return 当参数date1与date2都为null则返回0, 如果date1不为null而date2为空则返回1, 如果date1为null而date2不为空则返回-1
	 */
	public static int compare(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return 0;
		}
		if (date1 != null && date2 == null) {
			return 1;
		} else if (date1 == null) {
			return -1;
		}
		return date1.compareTo(date2);
	}

	/**
	 * 截取到天
	 */
	public static Date atStartOfDay(Date date) {
		if (date == null) {
			return null;
		}
		return truncate(date, Calendar.DAY_OF_MONTH).getTime();
	}

	/**
	 * 截取到小时
	 */
	public static Date atStartOfHour(Date date) {
		if (date == null) {
			return null;
		}
		return truncate(date, Calendar.HOUR_OF_DAY).getTime();
	}

	/**
	 * 截断, 最多截断到天
	 */
	private static Calendar truncate(Date date, int calendarField) {
		if (date == null) {
			return null;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (Calendar.MILLISECOND == calendarField) {
			return c;
		}
		c.set(Calendar.MILLISECOND, 0);
		if (Calendar.SECOND == calendarField) {
			return c;
		}
		c.set(Calendar.SECOND, 0);
		if (Calendar.MINUTE == calendarField) {
			return c;
		}
		c.set(Calendar.MINUTE, 0);
		if (Calendar.HOUR_OF_DAY == calendarField) {
			return c;
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		return c;
	}

	/**
	 * 获取当月第一天
	 */
	public static Date getMonthFirstDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获取当月最后一天
	 */
	public static Date getMonthLastDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获取下月第一天
	 */
	public static Date getNextMonthFirstDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获取下月最后一天
	 */
	public static Date getNextMonthLastDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.add(Calendar.MONTH, 2);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获取上月第一天
	 */
	public static Date getLastMonthFirstDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 获取上月最后一天
	 */
	public static Date getLastMonthLastDay(Date date) {
		Calendar c = truncate(date, Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	private TimeUtil() throws IllegalAccessException {
		throw new IllegalAccessException("不允许实例化");
	}
}
