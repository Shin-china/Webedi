package customer.sap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import cds.gen.sys.T11IfManager;
import customer.tool.Base64Util;

public class ApiCpiDao {

    private static final Logger logger = LoggerFactory.getLogger(ApiCpiDao.class);

    // public String getCpi(T11IfManager url, String code, Map<String, String> maps)
    // throws Exception {
    // // url =
    // //
    // "https://cpi-dev.it-cpi006-rt.cfapps.jp10.hana.ondemand.com/http/ManufacturingOrder";
    // // userDetail =
    // //
    // "c2ItZmFlZDJjZGMtODkzNy00ZGMwLTk3OTktNWZhZTU2NzFmMjI4IWIyNDd8aXQtcnQtY3BpLWRldiFiMTM0OnZKTkVUemFkRGsxaVFSc0h5V3l3YVlwVDBsVT0=";

    // String sendHttpPost = RestHttpClient.sendHttpPost(url, maps);
    // Map<String, String> map = new HashMap<>();
    // if (StringUtils.hasLength(sendHttpPost)) {
    // logger.info("CPI返回{}", sendHttpPost);
    // ReturnCPILog vo = JSONObject.parseObject(sendHttpPost, ReturnCPILog.class);
    // map.put("msgCode", "3");
    // map.put("msgText",
    // code + "全部：" + vo.getTotalNum() + ",成功：" + vo.getSuccessNum() + ", 失敗：" +
    // vo.getErrorNum());
    // } else {
    // map.put("msgCode", "1");
    // map.put("msgText", code + "インターフェースの呼び出しに失敗しました");
    // }
    // return JSONObject.toJSONString(map);
    // }

    public String getCpi(T11IfManager url, String code, String jsonString) throws Exception {
        // url =
        // "https://cpi-dev.it-cpi006-rt.cfapps.jp10.hana.ondemand.com/http/ManufacturingOrder";
        // userDetail =
        // "c2ItZmFlZDJjZGMtODkzNy00ZGMwLTk3OTktNWZhZTU2NzFmMjI4IWIyNDd8aXQtcnQtY3BpLWRldiFiMTM0OnZKTkVUemFkRGsxaVFSc0h5V3l3YVlwVDBsVT0=";
        String sendHttpPost = RestHttpClient.sendHttpPost(url, jsonString);
        Map<String, String> map = new HashMap<>();
        if (StringUtils.hasLength(sendHttpPost)) {
            logger.info("CPI返回{}", sendHttpPost);
            // ReturnCPILog vo = JSONObject.parseObject(sendHttpPost, ReturnCPILog.class);
            map.put("msgCode", "3");
            map.put("msgText",
                    sendHttpPost);
        } else {
            map.put("msgCode", "1");
            map.put("msgText", code + "インターフェースの呼び出しに失敗しました");
        }
        return JSONObject.toJSONString(map);
    }

}