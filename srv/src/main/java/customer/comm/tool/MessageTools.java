package customer.comm.tool;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class MessageTools {

    public static String getMsgText(ResourceBundleMessageSource rbms, String code) {

        if (code != null && rbms != null) {
            return rbms.getMessage(code, null, Locale.getDefault());
        }
        return code;

    }

    public static String getMsgText(ResourceBundleMessageSource rbms, String code, Object... para) {

        if (code != null && rbms != null) {
            return rbms.getMessage(code, para, Locale.getDefault());
        }
        return code;

    }

    public static String getMsgText(ResourceBundleMessageSource rbms, Locale local, String code, Object... para) {

        if (code != null && rbms != null) {
            return rbms.getMessage(code, para, local);
        }
        return code;

    }
}
