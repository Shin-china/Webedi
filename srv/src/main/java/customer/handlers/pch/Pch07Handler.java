package customer.handlers.pch;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.tableservice.PCH07CheckDATAContext;
import cds.gen.tableservice.PCH07SaveDATAContext;

import cds.gen.tableservice.TableService_;
import customer.bean.pch.Pch07DataList;
import customer.service.pch.Pch07Service;


import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch07Handler implements EventHandler {

    @Autowired
    ResourceBundleMessageSource rbms;
    @Autowired
    private Pch07Service Pch07Service;

    // check数据
    @On(event = "PCH07_CHECK_DATA")
    public void checkData(PCH07CheckDATAContext context) {
        Pch07DataList list = JSON.parseObject(context.getshelfJson(), Pch07DataList.class);
    Pch07Service.detailsCheck(list);
    context.setResult(JSON.toJSONString(list));
    }

      // 保存数据
  @On(event = "PCH07_SAVE_DATA")
    public void saveData(PCH07SaveDATAContext context) throws Exception{
      Pch07DataList list = JSON.parseObject(context.getshelfJson(), Pch07DataList.class);
      Pch07Service.detailsSave(list);
      context.setResult(JSON.toJSONString(list));
  }

}
