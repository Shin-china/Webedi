package customer.handlers.pch;

import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import org.apache.http.client.HttpClient;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.sys.T11IfManager;
import cds.gen.tableservice.PCH02ConfirmationREQUESTContext;
import cds.gen.tableservice.TableService_;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;
import customer.tool.Eenvironment;

import com.sap.cds.reflect.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch02Handler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(Pch02Handler.class);

    @Autowired
    private IFSManageDao ifsManageDao;

    @On(entity = PCH02ConfirmationREQUESTContext.CDS_NAME)
    public void onPCH02ConfirmationREQUEST(PCH02ConfirmationREQUESTContext context) {
        try {
            // 从前端参数中获取字符串
            String parms = context.getParms();

            // 解析前台传入的参数，将JSON字符串解析为HashMap
            HashMap<String, String> parameters = parseParameters(parms);

            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("MM028");

            if (webServiceConfig != null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.get(webServiceConfig, 0, parameters, null);

                // 将 Web Service 的响应结果返回给前台
                context.setResult(response);
            } else {
                // 如果没有找到配置，返回错误信息
                context.setResult("Web Service configuration not found.");
            }
        } catch (Exception e) {
            logger.error("Error occurred during PCH02ConfirmationREQUEST handling.", e);
            context.setResult("Error occurred: " + e.getMessage());
        }
    }

    // 解析前台传入的参数（假设为 JSON 格式字符串）
    private HashMap<String, String> parseParameters(String parms) {
        HashMap<String, String> parameters = new HashMap<>();
        if (parms != null && !parms.isEmpty()) {
            JSONObject jsonObject = JSON.parseObject(parms);
            for (String key : jsonObject.keySet()) {
                parameters.put(key, jsonObject.getString(key));
            }
        }
        return parameters;
    }
}
