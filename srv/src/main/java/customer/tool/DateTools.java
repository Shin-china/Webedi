package customer.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTools {

    public static final String FORMATTER_DATE = "yyyy-MM-dd";
    public static final String FORMATTER_DATE_8 = "yyyyMMdd";
    public static final String FORMATTER_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMATTER_DATETIME_14 = "yyyyMMddHHmmss";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMATTER_DATE);
    private static final DateTimeFormatter dateFormatter_8 = DateTimeFormatter.ofPattern(FORMATTER_DATE_8);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMATTER_DATETIME);

    // ZoneId.of( "UTC+9") 日本时区
    // ZoneId.of( "UTC+8") 中国时区
    public static Instant getInstantNow() { // 得到0时区时间戳。没有时区信息
        return Instant.now();
    }

    public static LocalDateTime getLocalDateTime() { // 得到0时区时间
        return LocalDateTime.now().withNano(0); // 设置精度不要纳秒
    }

    public static LocalDate getLocalDate() { // 得到0时区日期
        return LocalDate.now(ZoneOffset.ofHours(0)); // 设置精度不要纳秒
    }

    // public static LocalDate getUserLocalDate() { // 当前用户对应的时区日期（采番用）
    // return LocalDate.now(ConfigConstants.DEFFAULT_USER_ZONE);
    // }

    // 时间戳转换为时间 LocalDateTime(1606176000000) → datetime
    public static LocalDateTime longString2DateTime(String str, ZoneId zone) {
        if (str == null)
            return null;

        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String s1 = m.replaceAll("").trim();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(s1)), zone);
    }

    // 时间戳转日期 LocalDateTime(1606176000000) → date
    public static LocalDate longString2Date(String str, ZoneId zone) {
        if (StringTool.isNull(str))
            return null;
        return longString2DateTime(str, zone).toLocalDate();
    }

    // 日期转成字符串 date → 2020-01-01
    public static String date2String(LocalDate date) {
        if (date == null)
            return null;
        return date.format(dateFormatter);
    }

    /**
     * 从LocalDateTime对象中获取时间部分，并格式化为"HH:mm:ss"格式的字符串。
     * 
     * @param localDateTime 要处理的LocalDateTime对象
     * @return 格式化为"HH:mm:ss"的时间字符串
     */
    public static String getTimeAsString(Instant instant) {
        // 使用默认时区将Instant转换为ZonedDateTime

        ZoneId defaultZoneId = ZoneId.systemDefault();
        defaultZoneId = ZoneId.of("Asia/Tokyo");
        ZonedDateTime zonedDateTime = instant.atZone(defaultZoneId);

        // 从ZonedDateTime中提取LocalDateTime
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localDateTime.format(timeFormatter);
    }

    /**
     * 从LocalDateTime对象中获取LocalDate对象。
     * 
     * @param localDateTime 要处理的LocalDateTime对象
     * @return LocalDate对象
     */
    public static LocalDate getLocalDate(Instant instant) {

        // 使用默认时区将Instant转换为ZonedDateTime
        ZoneId defaultZoneId = ZoneId.systemDefault();
        defaultZoneId = ZoneId.of("Asia/Tokyo");
        ZonedDateTime zonedDateTime = instant.atZone(defaultZoneId);

        // 从ZonedDateTime中提取LocalDateTime
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        return localDateTime.toLocalDate();
    }

    // 日期转成字符串 date → 20200101
    public static String date2String8(LocalDate date) {
        if (date == null)
            return null;
        return date.format(dateFormatter_8);
    }

    // 时间戳转日期 2020-01-01 → date
    public static LocalDate string2Date(String str) {
        if (StringTool.isNull(str))
            return null;
        return LocalDate.parse(str, dateFormatter);
    }

    // 时间戳转日期 2020-01-01 → date
    public static LocalDate string8_2_Date(String str) {
        if (StringTool.isNull(str))
            return null;
        return LocalDate.parse(str, dateFormatter_8);
    }

    // 时间转成字符串 datetime → 2020-01-01 12:00:00
    public static String datetime2String(LocalDateTime datetime) {
        return datetime.format(dateTimeFormatter);
    }

    public static String datetime2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATTER_DATETIME);
        return sdf.format(date);
    }

    // 字符串转dateTime 2024-05-23T01:27:51.559Z
    public static LocalDateTime Iso86012DateTime(String str) throws ParseException {
        if (StringTool.isNull(str))
            return null;

        DateTimeFormatter dft = null;
        if (str.contains("Z")) {

            if (str.length() == 27) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            } else if (str.length() == 26) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
            } else if (str.length() == 25) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
            } else if (str.length() == 24) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            } else if (str.length() == 23) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
            } else if (str.length() == 22) {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
            } else {
                dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            }

        } else if (str.contains("T")) {
            dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        } else if (str.length() == 14) {
            dft = DateTimeFormatter.ofPattern(FORMATTER_DATETIME_14);
        } else {
            if (str.length() == 10) { // 只有日期值得時候
                str = str + " 00:00:00";
            }

            dft = DateTimeFormatter.ofPattern(FORMATTER_DATETIME);
        }

        LocalDateTime ldt = LocalDateTime.parse(str, dft);
        return ldt;
    }

    // 字符串转date '2024-09-01T00:00:00.000Z'
    public static LocalDate Iso86012Date(String str) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // 适用于解析 ISO 8601 格式的日期时间字符串

        // 解析字符串为 ZonedDateTime 对象
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(str, formatter);

        // 从 ZonedDateTime 中提取 LocalDate
        LocalDate localDate = zonedDateTime.toLocalDate();
        return localDate;
    }

    // java "2024-11-29 "将上面字符串转为LocalDate
    public static LocalDate stringToDate(String str) throws ParseException {
        // 定义一个DateTimeFormatter来解析日期字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 使用formatter解析字符串为LocalDate
        LocalDate localDate = LocalDate.parse(str, formatter);

        // 输出结果
        // System.out.println(localDate); // 输出: 2024-11-29

        return localDate;
    }

    // 时间转到+偏移量 转成install
    public static Instant dateTimeToInstant(LocalDateTime dateTime, ZoneOffset zone) {
        if (dateTime == null)
            return null;
        return dateTime.toInstant(zone);
    }

    // 求两个时间的差异
    // ChronoUnit.SECONDS 秒 ChronoUnit.MILLIS 毫秒 ChronoUnit.MINUTES 分
    public static long cy(Instant from, Instant to, ChronoUnit unit) {
        return unit.between(from, to);
    }

    /**
     * 判断给定的日期是否是今天之前的日期（不包括今天）
     * 
     * @param date 要判断的日期
     * @return 如果是今天之前的日期，则返回true；否则返回false
     */
    public static boolean isBeforeToday(LocalDate date) {
        LocalDate today = LocalDate.now();
        return date.isBefore(today);
    }
}
