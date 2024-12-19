package customer.comm.odata;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import customer.comm.constant.ConfigConstants;
import customer.comm.tool.DateTools;
import customer.comm.tool.StringTool;

public class OdateValueTool {

    // 判定删除标记
    public static String getTrue2Y(boolean isDel) {

        if (isDel)
            return "Y";
        return "N";
    }

    // 判定标记
    public static String getX2Y(String x) {
        if ("X".equals(x) || "x".equals(x))
            return "Y";
        return "N";
    }

    // 判定标记
    public static String getXW2Y(String x) {
        if ("X".equals(x) || "x".equals(x) || "w".equals(x) || "W".equals(x))
            return "Y";
        return "N";
    }

    // 判定标记
    public static boolean getX(String x) {
        if ("X".equals(x) || "x".equals(x))
            return true;
        return false;
    }

    // 判定多语言代码
    public static String getLocaleCode(String sapCode) {
        if (sapCode != null) {

            if ("1".equals(sapCode))
                return "zh_CN";
            if ("E".equals(sapCode))
                return "en_GB";
            if ("J".equals(sapCode))
                return "ja_JP";

            if ("ZH".equals(sapCode))
                return "zh_CN";
            if ("EN".equals(sapCode))
                return "en_GB"; // English - English
            if ("JA".equals(sapCode))
                return "ja_JP";
        }

        return sapCode;
    }

    /// Date(1711411200000)/ 转成 2023-01-01
    public static String Iso8601ToUtcDate(String dateString) {
        return DateTools.date2String(DateTools.longString2Date(dateString, ConfigConstants.S4_ODATA_ZONE_OFFSET));
    }

    // 将 SAP日期转换为本地日期。（此处为日本/中国日期）
    public static LocalDate Iso8601ToLocalDate(String dateString) {

        if (dateString != null) {

            return DateTools.longString2Date(dateString.replace("+0000", ""), ConfigConstants.DEFFAULT_USER_ZONE);
        }
        return null;

        // return DateTools.longString2Date(dateString,
        // ConfigConstants.DEFFAULT_USER_ZONE);

    }

    public static String iso8601ToUtcTime(String time8601) {
        if (time8601 != null && time8601.length() == 11) {
            return time8601.substring(2, 4) + ":" + time8601.substring(5, 7) + ":" + time8601.substring(8, 10);
        }
        return "00.00.00";
    }

    // 值： /Date(1711497600000)/ +PT07H48M16S 转成日期时间
    public static String Iso8601_2DateTime(String dateString, String timeDu) {
        if (StringTool.isNull(dateString) || StringTool.isNull(timeDu))
            return null;
        return Iso8601ToUtcDate(dateString) + " " + iso8601ToUtcTime(timeDu);
    }

    // 字符串转时间
    // 字符串可能性：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    // 字符串可能性：yyyy-MM-dd'T'HH:mm:ss.SSS
    // 字符串可能性：yyyy-MM-dd'T'HH:mm:ss
    public static Instant ISO8601ToInstant(String str) throws ParseException {
        return DateTools.dateTimeToInstant(DateTools.Iso86012DateTime(str), ConfigConstants.S4_ODATA_ZONE_OFFSET);
    }

    public static Instant getMaxInstant(Instant startValue, Instant newValue) {
        if (startValue == null)
            return newValue;

        if (startValue != null && newValue != null && newValue.isAfter(startValue)) {
            return newValue;
        }

        return startValue;
    }

    // 传入 SAP的0时区 日期+时间
    // 输出 当前用户主要时区的 日期
    public static LocalDate UTCTime_2_USER_Zone_Date(String dateString, String timeDu) throws ParseException {
        String deloiverTime = Iso8601_2DateTime(dateString, timeDu);
        if (deloiverTime == null)
            return null;
        LocalDateTime deloiverDateTime = DateTools.Iso86012DateTime(deloiverTime);
        deloiverDateTime = DateTools.toZone(deloiverDateTime, ConfigConstants.DEFFAULT_USER_ZONE);
        return deloiverDateTime.toLocalDate();
    }
}
