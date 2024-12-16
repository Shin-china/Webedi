package customer.comm.tool;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import customer.service.comm.SpringUtil;

public class Eenvironment {

    private static final Logger log = LoggerFactory.getLogger(SpringUtil.class);

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

    public static String classFlorder_2_log_florder(String f) {
        String logFileWin = "\\logs\\usap";
        String logFileLin = "//logs//usap";
        log.info("系统资源路径：" + f);
        if (isWindows()) {
            for (int i = 0; i < 3; i++) {
                File threePath = new File(f);
                f = threePath.getParent();
            }
            f += logFileWin;
        }
        else {
            for (int i = 0; i < 2; i++) {
                File twoPath = new File(f);
                f = twoPath.getParent();
            }
            f += logFileLin;
        }
        return f;
    }

    public static String getFileEncode() {
        Properties props = System.getProperties(); // 系统属性
        return props.getProperty("file.encoding");
    }

}