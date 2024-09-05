package customer.comm.tool;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTools {

    public static final String FORMATTER_DATE = "yyyy-MM-dd";
    public static final String FORMATTER_DATE_8 = "yyyyMMdd";
    public static final String FORMATTER_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMATTER_DATE);
    private static final DateTimeFormatter dateFormatter_8 = DateTimeFormatter.ofPattern(FORMATTER_DATE_8);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMATTER_DATETIME);

    //ZoneId.of( "UTC+9")  日本时区
    //ZoneId.of( "UTC+8")  中国时区
    public static Instant getInstantNow() { //得到0时区时间戳。没有时区信息
        return Instant.now();
    }

    // public static LocalDateTime getLocalDateTime() { //得到0时区时间
    //     return LocalDateTime.now().withNano(0); //设置精度不要纳秒
    // }

    // // public static LocalDate getUserLocalDate() { //当前用户对应的时区日期（采番用）
    // //     return LocalDate.now(ConfigConstants.DEFFAULT_USER_ZONE);
    // // }

    // //时间戳转换为时间 LocalDateTime(1606176000000)  → datetime
    // public static LocalDateTime longString2DateTime(String str, ZoneId zone) {
    //     if (str == null)
    //         return null;

    //     String regEx = "[^0-9]";
    //     Pattern p = Pattern.compile(regEx);
    //     Matcher m = p.matcher(str);
    //     String s1 = m.replaceAll("").trim();
    //     return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(s1)), zone);
    // }

    // //时间戳转日期 LocalDateTime(1606176000000)  → date
    // // public static LocalDate longString2Date(String str, ZoneId zone) {
    // //     if (StringTool.isNull(str))
    // //         return null;
    // //     return longString2DateTime(str, zone).toLocalDate();
    // // }

    // //日期转成字符串    date → 2020-01-01
    // public static String date2String(LocalDate date) {
    //     if (date == null)
    //         return null;
    //     return date.format(dateFormatter);
    // }

    // //日期转成字符串    date → 20200101
    // public static String date2String8(LocalDate date) {
    //     if (date == null)
    //         return null;
    //     return date.format(dateFormatter_8);
    // }

    // //时间戳转日期 2020-01-01 → date
    // public static LocalDate string2Date(String str) {
    //     if (StringTool.isNull(str))
    //         return null;
    //     return LocalDate.parse(str, dateFormatter);
    // }

    // //时间转成字符串    datetime → 2020-01-01 12:00:00
    // public static String datetime2String(LocalDateTime datetime) {
    //     return datetime.format(dateTimeFormatter);
    // }

    // //字符串转dateTime 2024-05-23T01:27:51.559Z
    // public static LocalDateTime Iso86012DateTime(String str) throws ParseException {
    //     if (StringTool.isNull(str))
    //         return null;

    //     DateTimeFormatter dft = null;
    //     if (str.contains("Z")) {

    //         if (str.length() == 27) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    //         }
    //         else if (str.length() == 26) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'");
    //         }
    //         else if (str.length() == 25) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");
    //         }
    //         else if (str.length() == 24) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //         }
    //         else if (str.length() == 23) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");
    //         }
    //         else if (str.length() == 22) {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    //         }
    //         else {
    //             dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    //         }

    //     }
    //     else if (str.contains("T")) {
    //         dft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    //     }
    //     else {
    //         if (str.length() == 10) { //只有日期值得時候
    //             str = str + " 00:00:00";
    //         }

    //         dft = DateTimeFormatter.ofPattern(FORMATTER_DATETIME);
    //     }

    //     LocalDateTime ldt = LocalDateTime.parse(str, dft);
    //     return ldt;
    // }

    // //时间转到+偏移量 转成install
    // public static Instant dateTimeToInstant(LocalDateTime dateTime, ZoneOffset zone) {
    //     if (dateTime == null)
    //         return null;
    //     return dateTime.toInstant(zone);
    // }

    // //求两个时间的差异
    // //ChronoUnit.SECONDS  秒  ChronoUnit.MILLIS  毫秒  ChronoUnit.MINUTES 分
    // public static long cy(Instant from, Instant to, ChronoUnit unit) {
    //     return unit.between(from, to);
    // }

}