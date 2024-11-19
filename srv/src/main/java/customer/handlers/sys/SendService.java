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

import customer.bean.sys.Ifm054Bean;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;

import com.google.common.io.ByteStreams;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.tableservice.PchT06QuotationH;
import cds.gen.tableservice.PchT07QuotationD;
import cds.gen.tableservice.Pch06BatchSendingContext;
import cds.gen.tableservice.TableService_;
import cds.gen.AttachmentJson;
import cds.gen.common.*;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;

@Component
public class SendService {

    @Autowired
    PchD006 PchD006;
    @Autowired
    PchD007 PchD007;
    @Autowired
    DocNoDao docNoDao;

    // 发送t06数据，传入json,返回ArrayList<T06QuotationH>
    public ArrayList<T06QuotationH> getJson(String json) {
        ArrayList<T06QuotationH> pch06List = new ArrayList<>();
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(json);
        // 根据传入的po和po明细修改po明细状态
        // 获取要传入的字符串
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            T06QuotationH t06QuotationH = PchD006.get(jsonObject.getString("QUO_NUMBER"));
            pch06List.add(t06QuotationH);
        }
        return pch06List;

    }
}
