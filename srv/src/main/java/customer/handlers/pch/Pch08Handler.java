package customer.handlers.pch;

import cds.gen.tableservice.PCH06SaveDATAContext;
import cds.gen.tableservice.PCH08SaveDATAContext;
import cds.gen.tableservice.TableService_;
import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import customer.bean.pch.Pch08DataList;
import customer.service.pch.Pch08Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch08Handler implements EventHandler {

    @Autowired
    ResourceBundleMessageSource rbms;
    @Autowired
    private Pch08Service pchService;

//    // check数据
//    @On(event = "PCH07_CHECK_DATA")
//    public void checkData(PCH07CheckDATAContext context) {
//        Pch07DataList list = JSON.parseObject(context.getShelfJson(), Pch07DataList.class);
//    Pch07Service.detailsCheck(list);
//    context.setResult(JSON.toJSONString(list));
//    }
//
//      // 保存数据
//  @On(event = "PCH07_SAVE_DATA")
//    public void saveData(PCH07SaveDATAContext context) {
//        Pch07DataList list = JSON.parseObject(context.getShelfJson(), Pch07DataList.class);
//    // Pch07Service.detailsSave(list);
//    context.setResult(JSON.toJSONString(list));
//  }

    // 保存数据
    @On(event = "PCH08_SAVE_DATA")
    public void saveData(PCH08SaveDATAContext context) throws ParseException {
        Pch08DataList list = JSON.parseObject(context.getStr(), Pch08DataList.class);
        pchService.check(list);
        if (!list.getErr()) {
            pchService.detailsSave(list);
        }

        context.setResult(JSON.toJSONString(list));
    }

}
