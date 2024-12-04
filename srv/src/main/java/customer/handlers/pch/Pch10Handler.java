package customer.handlers.pch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.xmlbeans.StringEnumAbstractBase.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.pch.T06QuotationH;
import cds.gen.tableservice.PCH10GrSENDContext;
import cds.gen.tableservice.PCH10Header;
import cds.gen.tableservice.PCH10Header_;
import cds.gen.tableservice.PCH10SaveDATAContext;
import cds.gen.tableservice.Pch06BatchSendingContext;
import cds.gen.tableservice.TableService_;
import customer.bean.pch.Pch06DataList;
import customer.bean.pch.Pch10SaveDataList;
import customer.handlers.sys.SendService;
import customer.service.pch.Pch10Service;

import com.sap.cds.services.cds.CdsReadEventContext;

@Component
@ServiceName(TableService_.CDS_NAME)

public class Pch10Handler implements EventHandler {

    @Autowired
    private Pch10Service pch10Service;
    @Autowired
    SendService sendService;

    @After(entity = PCH10Header_.CDS_NAME, event = CqnService.EVENT_READ)
    public void AfterreadPch10Header(CdsReadEventContext context, Stream<PCH10Header> pch10Header) {

        List<PCH10Header> distinctlist = pch10Header.distinct().collect(Collectors.toList());

        context.setResult(distinctlist);
    }

    @On(event = "PCH10_GR_SEND")
    public void Send(PCH10GrSENDContext context) throws IOException {

        JSONArray jsonArray = JSONArray.parseArray(context.getParams());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String sal_Num = jsonObject.getString("Quo_No");

            // 通过quo_no获取客户信息，并设置客户的报价单状态
            pch10Service.setQuostatus(sal_Num);

            context.setResult("1");

        }
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

    // 保存
    @On(event = "PCH10_SAVE_DATA")
    public void saveData(PCH10SaveDATAContext context) throws IOException {

        Pch10SaveDataList list = JSON.parseObject(context.getJson(), Pch10SaveDataList.class);

        pch10Service.check(list);
        if (!list.getErr()) {
            pch10Service.detailsSave(list);
        }

        context.setResult(JSON.toJSONString(list));
    }
}
