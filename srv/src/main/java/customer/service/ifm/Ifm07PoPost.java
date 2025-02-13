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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.handler.annotations.On;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import customer.bean.sys.Ifm054Bean;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.BaseMoveService;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;
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
   
   
    public String poPost(IFLog log) {

        // jobMonotor.poolMonitor3();
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_COM);
        

        try {
            ifLog.setTd(super.transactionInit()); // 事务初始换
            this.insertLog(ifLog);
            // 从前端参数中获取字符串
            String parms = context.getParms();

            // 解析前台传入的参数，将JSON字符串解析为HashMap
            String parameters = parseParameters2(parms);

            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("IF043");

            if (webServiceConfig != null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.post(webServiceConfig, parameters, null);
                JSONObject object = JSONObject.parseObject(response);
                System.out.println(StringTool.convertGBKToUTF8(object.getString("message")));
                // 获取 Web Service 返回的 status 字段
                String status = object.getString("status");
                // 判断 status 是否为 "200"
                if ("200".equals(status)) {
                    // 如果 status 为 "200"，表示成功，执行 PO 明细状态更新
                    pchService.updatePch03(context.getParms());
                }
                return  object.getString("message");
            } else {
                // 如果没有找到配置，返回错误信息
                return "Web Service configuration not found.";
            }
        } catch (Exception e) {

            logger.error("Error occurred during PCH02ConfirmationREQUEST handling.", e);
            return "Error occurred: " + e.getMessage();
        }
    }
}
