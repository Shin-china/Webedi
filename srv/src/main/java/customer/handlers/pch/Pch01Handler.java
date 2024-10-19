package customer.handlers.pch;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH01CheckDATAContext;
import cds.gen.tableservice.PCH01SaveDATAContext;
import cds.gen.tableservice.TableService_;
import customer.bean.pch.Pch01List;
import customer.service.ifm.Ifm02MstService;
import customer.service.pch.Pch01Service;
import customer.task.JobMonotor;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch01Handler implements EventHandler {

  @Autowired
  ResourceBundleMessageSource rbms;

  @Autowired
  private Pch01Service Pch01Service;

  // 测试使用的
  // @Autowired
  // private JobMonotor JobMonotor;

  // check数据
  @On(event = "PCH01_CHECK_DATA")
  public void checkData(PCH01CheckDATAContext context) throws IOException {
    Pch01List list = JSON.parseObject(context.getShelfJson(), Pch01List.class);
    // JobMonotor.poolMonitor();
    Pch01Service.detailsCheck(list);
    context.setResult(JSON.toJSONString(list));
  }

  // 保存数据
  @On(event = "PCH01_SAVE_DATA")
  public void saveData(PCH01SaveDATAContext context) {
    Pch01List list = JSON.parseObject(context.getShelfJson(), Pch01List.class);
    Pch01Service.detailsSave(list);
    context.setResult(JSON.toJSONString(list));
  }

}
