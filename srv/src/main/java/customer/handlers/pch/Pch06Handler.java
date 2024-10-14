package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH03SENDEMAILContext;
import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PCH06SaveDATAContext;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.bean.pch.Pch06DataList;
import customer.service.pch.Pch06Service;
import customer.service.sys.EmailServiceFun;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch06Handler implements EventHandler {

    @Autowired
    private Pch06Service pchService;

    // 保存数据
    @On(event = "PCH06_SAVE_DATA")
    public void saveData(PCH06SaveDATAContext context) {
        Pch06DataList list = JSON.parseObject(context.getStr(), Pch06DataList.class);
        pchService.check(list);
        pchService.detailsSave(list);
        context.setResult(JSON.toJSONString(list));
    }
}
