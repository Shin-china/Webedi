package customer.handlers.sys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.alibaba.excel.write.metadata.fill.FillConfig;

import com.google.common.io.ByteStreams;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.AttachmentJson;
import cds.gen.common.*;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T13Attachment;
import customer.bean.com.UmcConstants;
import customer.dao.pch.Pch08Dao;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;
import customer.dao.sys.T13AttachmentDao;
import customer.odata.BaseMoveService;
import customer.service.sys.ObjectStoreService;

@Component
@ServiceName(Common_.CDS_NAME)
public class CommonHandler implements EventHandler {

    @Autowired
    private ObjectStoreService objectStoreService;
    @Autowired
    private PchD006 PchD006;
    @Autowired
    private PchD007 PchD007;
    @Autowired
    private T13AttachmentDao t13AttachmentDao;
    @Autowired
    DocNoDao docNoDao;
    @Autowired
    SendService sendService;
    private static final Logger logger = LoggerFactory.getLogger(CommonHandler.class);

    // IFM054 購買見積依頼受信+送信
    @On(event = "pch06BatchImport")
    public void pch06BatchImport(Pch06BatchImportContext context) throws Exception {
        // 获取uqmc传入的t06数据
        // 获取
        System.out.println(JSONObject.toJSONString(context.getPch06()));
        // System.out.println(context.getJson());

        // Ifm054Bean list = JSON.parseObject(context.getJson(), Ifm054Bean.class);

        // 将 Collection 转换为 Listpch06BatchImport
        ArrayList<PchT06QuotationH> pch06List = new ArrayList<>(context.getPch06());
        ArrayList<cds.gen.pch.T06QuotationH> pch06List2 = new ArrayList<>();
        String msg = "";
        try {
            // 提取数据，插入表中
            sendService.extracted(pch06List, pch06List2);

            msg = sendService.sendPost(pch06List2);
        } catch (Exception e) {
            msg = UmcConstants.ERROR;
        }

        System.out.println(msg);

        context.setResult(msg);
    }

    // IFM055 購買見積依頼送信
    @On(event = "pch06BatchSending")
    public void pch06BatchSending(Pch06BatchSendingContext context) throws Exception {
        ArrayList<T06QuotationH> pch06List = new ArrayList<>();
        String msg = "";
        try {
            pch06List = sendService.getJson(context.getJson());

            // 调用接口传值
            if (pch06List != null && pch06List.size() > 0) {
                msg = sendService.sendPost(pch06List);
                System.out.println(msg);
            }

        } catch (Exception e) {
            logger.info(UmcConstants.ERROR + e.getMessage());
            context.setResult(UmcConstants.ERROR + e.getMessage());
        }
        context.setResult(msg);
    }

}
