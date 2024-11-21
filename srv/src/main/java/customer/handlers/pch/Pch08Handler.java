package customer.handlers.pch;

import cds.gen.tableservice.*;
import com.alibaba.fastjson.JSON;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import customer.bean.pch.Pch08DataList;

import customer.service.pch.Pch08Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.text.ParseException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch08Handler implements EventHandler {

    @Autowired
    ResourceBundleMessageSource rbms;
    @Autowired
    private Pch08Service pch08Service;

    // // check数据
    // @On(event = "PCH07_CHECK_DATA")
    // public void checkData(PCH07CheckDATAContext context) {
    // Pch07DataList list = JSON.parseObject(context.getShelfJson(),
    // Pch07DataList.class);
    // Pch07Service.detailsCheck(list);
    // context.setResult(JSON.toJSONString(list));
    // }
    //
    // // 保存数据
    // @On(event = "PCH07_SAVE_DATA")
    // public void saveData(PCH07SaveDATAContext context) {
    // Pch07DataList list = JSON.parseObject(context.getShelfJson(),
    // Pch07DataList.class);
    // // Pch07Service.detailsSave(list);
    // context.setResult(JSON.toJSONString(list));
    // }

    // 保存数据
    @On(event = "PCH08_SAVE_DATA")
    public void saveData(PCH08SaveDATAContext context) throws ParseException {
        Pch08DataList list = JSON.parseObject(context.getStr(), Pch08DataList.class);
        pch08Service.check(list);
        if (!list.getErr()) {
            pch08Service.detailsSave(list);
        }

        context.setResult(JSON.toJSONString(list));
    }

    @On(event = PCH08ShowDETAILContext.CDS_NAME)
    public void showDetail(PCH08ShowDETAILContext context) {
        List<Map<String, Object>> detailData = pch08Service.getDetailData(context.getParam());
        context.setResult(JSON.toJSONString(detailData));
    }

    @On(event = PCH08EditDETAILContext.CDS_NAME)
    public void updateDetail(PCH08EditDETAILContext context) {
        String param = context.getParam();
        pch08Service.updateDetail(param);
        context.setResult("success");
    }

}
