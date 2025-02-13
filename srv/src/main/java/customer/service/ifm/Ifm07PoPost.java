package customer.service.ifm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.handler.annotations.On;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import org.json.JSONArray;
import customer.bean.sys.Ifm054Bean;
import customer.comm.tool.MessageTools;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.BaseMoveService;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;
import customer.service.pch.PchService;
import customer.tool.StringTool;
import 
cds.gen.common.PchT06QuotationH;
import cds.gen.common.PchT07QuotationD;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T11IfManager;
import cds.gen.tableservice.PCH02ConfirmationREQUESTContext;
import customer.bean.com.UmcConstants;
import customer.bean.ifm.IFLog;
import customer.bean.pch.Pch07;

@Component
public class Ifm07PoPost extends IfmService{
    private static final Logger logger = LoggerFactory.getLogger(Ifm07PoPost.class);
   
    @Autowired
    private PchService pchService;

   // 从前端参数中获取字符串
    public String poPost(IFLog log,String parms) {

        log.setTd(super.transactionInit()); // 事务初始换
        TransactionStatus s = null;
        try {
            this.insertLog(log);
                    

        
            s = this.begin(log.getTd());
            T11IfManager info = this.getIfMnager(log);
            // 解析前台传入的参数，将JSON字符串解析为HashMap
            String parameters = parseParameters2(parms,log);
            //将传入参数写表
            log.gett15log().setIfPara(JSON.toJSONString(parameters));
            // 获取 Web Service 配置信息

            if (info!= null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.post(info, parameters, null);
                JSONObject object = JSONObject.parseObject(response);
                System.out.println(StringTool.convertGBKToUTF8(object.getString("message")));
                // 获取 Web Service 返回的 status 字段
                String status = object.getString("status");
                // 判断 status 是否为 "200"
                if ("200".equals(status)) {
                    // 如果 status 为 "200"，表示成功，执行 PO 明细状态更新
                    pchService.updatePch03(parms);
                    log.addSuccessCount();
                    
                    
                }
                log.setSuccessMsg(object.getString("message"));
                this.updateLog(log);
                this.commit(s);
                
                return  object.getString("message");
            } else {
                // 如果没有找到配置，返回错误信息
                return "Web Service configuration not found.";
            }
        } catch (Exception e) {
            log.setFairMsg("Error occurred: " + e.getMessage());
            log.addErrorCount();
            this.updateLog(log);
            this.rollback(s);
            logger.error("Error occurred during PCH02ConfirmationREQUEST handling.", e);
            return "Error occurred: " + e.getMessage();
        }
    }
        // 解析前台传入的参数（假设为 JSON 格式字符串）
        private String parseParameters2(String parms,IFLog log) {
            JSONArray stringToJsonArray = new JSONArray();
            HashMap<String, String> parameters = new HashMap<>();
            if (parms != null && !parms.isEmpty()) {
                // DeliveryInfoList object = JSON.parseObject(parms,DeliveryInfoList.class);
                // List<DeliveryInfo> items = object.getItems();
                stringToJsonArray = stringToJsonArray(parms);
                stringToJsonArray.toString();
                System.out.println();
    
                // parameters.put("items", );
    
            }
            return stringToJsonArray.toString();
        }
    // 定义一个方法来将String类型的JSON数组转换成JSONArray
    public JSONArray stringToJsonArray(String jsonString) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
