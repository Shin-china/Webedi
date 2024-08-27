package customer.tool;

import java.util.Properties;

public class Eenvironment {

    // 操作系统的名称
    public static String getOsName() {
        Properties props = System.getProperties(); // 系统属性
        return props.getProperty("os.name");
    }

    // 是否是windows操作系统
    public static boolean isWindows() {
        String osName = Eenvironment.getOsName();
        if (osName.toUpperCase().startsWith("W")) {
            return true;
        }
        return false;
    }

    // 得到文件系统分隔符
    // 在 unix 系统中是”／” Windows：\
    public static String getFileSeparator() {
        Properties props = System.getProperties(); // 系统属性
        return props.getProperty("file.separator");
    }

}