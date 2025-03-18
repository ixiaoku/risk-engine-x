package risk.engine.common.util;

/**
 * @Author: X
 * @Date: 2025/3/18 15:11
 * @Version: 1.0
 */
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

    // 常用日期格式
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // DateTimeFormatter 实例
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

    /**
     * 获取当前时间的字符串（默认格式 yyyy-MM-dd HH:mm:ss）
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期的字符串（默认格式 yyyy-MM-dd）
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间的字符串（默认格式 HH:mm:ss）
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(TIME_FORMATTER);
    }

    /**
     * 时间戳（毫秒）转换为 LocalDateTime
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String getTimeByTimestamp(long timestamp) {
        LocalDateTime localDateTime = timestampToLocalDateTime(timestamp);
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * LocalDateTime 转时间戳（毫秒）
     */
    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 字符串转换为 LocalDateTime（格式：yyyy-MM-dd HH:mm:ss）
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 字符串转换为 LocalDate（格式：yyyy-MM-dd）
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
    }

    /**
     * 计算两个时间之间的秒数
     */
    public static long secondsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return Duration.between(startTime, endTime).getSeconds();
    }

    /**
     * 给 LocalDateTime 增加指定天数
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    /**
     * 给 LocalDateTime 增加指定小时
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * Date 转 LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime 转 Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        // 测试时间工具类
        System.out.println("当前日期时间: " + getCurrentDateTime());
        System.out.println("当前日期: " + getCurrentDate());
        System.out.println("当前时间: " + getCurrentTime());

        long timestamp = System.currentTimeMillis();
        System.out.println("时间戳转 LocalDateTime: " + timestampToLocalDateTime(timestamp));

        LocalDateTime now = LocalDateTime.now();
        System.out.println("LocalDateTime 转时间戳: " + localDateTimeToTimestamp(now));

        System.out.println("字符串转 LocalDateTime: " + parseDateTime("2025-03-18 14:30:00"));
        System.out.println("字符串转 LocalDate: " + parseDate("2025-03-18"));

        System.out.println("计算日期差: " + daysBetween(LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 18)) + " 天");
        System.out.println("计算秒数差: " + secondsBetween(now, now.plusMinutes(10)) + " 秒");

        System.out.println("增加 5 天: " + addDays(now, 5));
        System.out.println("增加 3 小时: " + addHours(now, 3));

        Date date = new Date();
        System.out.println("Date 转 LocalDateTime: " + dateToLocalDateTime(date));
        System.out.println("LocalDateTime 转 Date: " + localDateTimeToDate(now));
    }
}
