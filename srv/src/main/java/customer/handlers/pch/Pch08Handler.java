package customer.handlers.pch;

import cds.gen.tableservice.*;
import com.alibaba.fastjson.JSON;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import customer.bean.pch.Pch08DataList;

import customer.bean.pch.Pch08Template;
import customer.bean.pch.Pch08UploadResult;
import customer.service.pch.Pch08Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.text.ParseException;

import java.util.List;
import java.util.Map;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch08Handler implements EventHandler {

    @Autowired
    ResourceBundleMessageSource rbms;
    @Autowired
    private Pch08Service pch08Service;

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

    @On(event = PCH08DownloadTEMPLATEContext.CDS_NAME)
    public void downloadTemplate(PCH08DownloadTEMPLATEContext context) {
        String param = context.getParam();
        List<Pch08Template> dataList = pch08Service.downloadTemplate(param);
        context.setResult(JSON.toJSONString(dataList));
    }

    @On(event = PCH08UploadCHECKContext.CDS_NAME)
    public void uploadCheck(PCH08UploadCHECKContext context) {
        String param = context.getParam();
        Pch08UploadResult resultList = pch08Service.upload(param, true);
        context.setResult(JSON.toJSONString(resultList));
    }

    @On(event = PCH08UploadEXECUTEContext.CDS_NAME)
    public void uploadExecute(PCH08UploadEXECUTEContext context) {
        String param = context.getParam();
        Pch08UploadResult resultList = pch08Service.upload(param, false);
        context.setResult(JSON.toJSONString(resultList));
    }

}
