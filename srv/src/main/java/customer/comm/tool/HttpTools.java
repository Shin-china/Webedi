package customer.comm.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.logging.log4j.util.Base64Util;

import cds.gen.sys.T11IfManager;
import customer.comm.base.MyToken;

public class HttpTools {

    public static HashMap<String, MyToken> tokenMap = new HashMap<String, MyToken>();

    public static String InputStream2String(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        //br.close();
        //inputStream.close();
        return sb.toString();
    }

    public static String getBaiscAuthorization(T11IfManager info) {
        String userInfo = Base64Util.encode(info.getSapuser());// 用户名,密码
        return "Basic " + userInfo;
    }

    public static String getOuth2Authorization(String token) {
        return "bearer " + token;
    }

    //清除内存中保留的token信息
    public static void clearTokenInfo(String id) throws IOException {
        tokenMap.put(id, null);
    }

}
