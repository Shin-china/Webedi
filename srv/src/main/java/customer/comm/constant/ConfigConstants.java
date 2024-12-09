package customer.comm.constant;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class ConfigConstants {

    public static List<String> SYSTEM_PLANT_LIST = new ArrayList<String>(); // 当前系统有哪些工厂

    public static ZoneOffset S4_ODATA_ZONE_OFFSET = ZoneOffset.ofHours(0); // 设定SAP ODATA传输值的 ZONE.

    public static ZoneOffset SERVER_ZONE = ZoneOffset.ofHours(0);// 服务器端ZONE.

    public static ZoneOffset DEFFAULT_USER_ZONE = ZoneOffset.ofHours(9);// 默认用户的时区.

    public static String BTP_APP_ID;// 当前APP在BTP的名字

    public static String OBJECT_STOTE_PDF_FLORD = ""; // 存储文件的ID的前缀（OB中没有目录。用此当成目录）

    public static String OBJECT_STOTE_TXT_FLORD = ""; // 存储文件的ID的前缀（OB中没有目录。用此当成目录）

    public static String OBJECT_STORE_TEMPLATE = ""; // 存储文件的ID的前缀（OB中没有目录。用此当成目录）

    public static String USER_LANG_CODE; // 当前系统的主要用户的语言。

    public static Integer HT_SERRSION_TIME_OUT = 60 * 180; // HT session 超时时间 180分钟

}
