package customer.tool;

import java.util.UUID;

public class UniqueIDTool {

    /**
     * 生成36位UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().toLowerCase();
    }

    /**
     * 生成32位UUID 去掉 "-"
     */
    public static String getUUID32() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }

    public static String convert32to36(String uuid) {
        String str = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
                + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);

        return str.toLowerCase();
    }

}