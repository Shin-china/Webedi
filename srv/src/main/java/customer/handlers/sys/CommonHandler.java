package customer.handlers.sys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    // IFM054 購買見積依頼受信+送信
    @On(event = "pch06BatchImport")
    public void pch06BatchImport(Pch06BatchImportContext context) {
        // 获取uqmc传入的t06数据
        // 获取
        System.out.println(JSONObject.toJSONString(context.getPch06()));
        // System.out.println(context.getJson());

        // Ifm054Bean list = JSON.parseObject(context.getJson(), Ifm054Bean.class);

        // 将 Collection 转换为 Listpch06BatchImport
        List<PchT06QuotationH> pch06List = new ArrayList<>(context.getPch06());
        pch06List.forEach(pchT06QuotationH -> {

            try {
                // 获取購買見積番号
                // pchT06QuotationH.setQuoNumber(docNoDao.getPJNo(1));
                pchT06QuotationH.setQuoNumber("1006");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 插入头标，首先删除原key值数据
            T06QuotationH t06QuotationH = T06QuotationH.create();

            // 复制类属性
            BeanUtils.copyProperties(pchT06QuotationH, t06QuotationH);
            // 如果已经存在则更新，如果不存在则插入
            T06QuotationH byID = PchD006.getByIdOnle(t06QuotationH.getId());
            ;
            t06QuotationH.remove("TO_ITEMS");
            if (byID != null) {
                PchD006.update(t06QuotationH);
            } else {
                PchD006.insert(t06QuotationH);
            }

            // 插入明细
            List<PchT07QuotationD> toItems = pchT06QuotationH.getToItems();
            toItems.forEach(pchT07QuotationD -> {
                // 获取購買見積番号
                pchT07QuotationD.setQuoNumber(pchT06QuotationH.getQuoNumber());

                T07QuotationD t07QuotationD = T07QuotationD.create();

                // // 复制类属性
                BeanUtils.copyProperties(pchT07QuotationD, t07QuotationD);
                // // 如果已经存在则更新，如果不存在则插入
                T07QuotationD byID2 = PchD007.getByT07Id(t07QuotationD.getId());

                if (byID2 != null) {
                    PchD007.update(t07QuotationD);
                } else {
                    PchD007.insert(t07QuotationD);
                }
            });

        });
        System.out.println("返回成功" + JSONObject.toJSONString(pch06List));
        // context.setResult(JSONObject.toJSONString(pch06List));
    }

    // IFM055 購買見積依頼送信
    @On(event = "pch06BatchSending")
    public void pch06BatchSending(Pch06BatchSendingContext context) {
        ArrayList<T06QuotationH> pch06List = new ArrayList<>();

        try {
            pch06List = sendService.getJson(context.getJson());

            // 调用接口传值

        } catch (Exception e) {
            context.setResult("失败");
        }

        context.setResult(JSON.toJSONString(pch06List));
    }

}
