package customer.handlers.sys;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.SYS03DeleteITEMSContext;
import cds.gen.tableservice.SysT07ComOpH;
import cds.gen.tableservice.SysT07ComOpH_;
import cds.gen.tableservice.SysT08ComOpD;
import cds.gen.tableservice.TableService_;
import customer.comm.tool.StringTool;
import customer.service.sys.SysDictService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys03Handler implements EventHandler {

  // @Autowired
  private DraftService adminService;

  @Autowired
  SysDictService sysDictService;

  Sys03Handler(@Qualifier(TableService_.CDS_NAME) DraftService adminService) {
    this.adminService = adminService;
  }

  /**
   * 
   * 检查修改后抬头
   * 
   * @param context       传入上下文
   * @param d012MoveActHs 传入画面输入值
   */
  @Before(entity = SysT07ComOpH_.CDS_NAME, event = CqnService.EVENT_UPDATE)
  public void beforeUpdateT07H(CdsUpdateEventContext context, Stream<SysT07ComOpH> t07Hs) {
    t07Hs.forEach(t07 -> {
      sysDictService.checkHcode(t07.getHCode(), t07.getId());
      // 检查头
      List<SysT08ComOpD> toItems = t07.getToItems();
      

      for (SysT08ComOpD d : toItems) {
        d.setHId(t07.getId());
        d.setHCode(t07.getHCode());
      }
    });

  }

  @Before(entity = SysT07ComOpH_.CDS_NAME, event = CqnService.EVENT_CREATE)
  public void beforeCreateT07H(CdsCreateEventContext context, Stream<SysT07ComOpH> t07Hs) {
    // code check 重复
    t07Hs.forEach(t07 -> {
      sysDictService.checkHcode(t07.getHCode(), t07.getId());

      List<SysT08ComOpD> toItems = t07.getToItems();
      sysDictService.checkItems(toItems);
      for (SysT08ComOpD t08 : toItems) {
        t08.setHCode(t07.getHCode());
      }

    });
  }

  @On(event = SYS03DeleteITEMSContext.CDS_NAME)
  public void deleteItems(SYS03DeleteITEMSContext context) {

    // 按照，分割多个明细ID
    String[] idArrsplit = context.getParms().split(",");
    for (int i = 0; i < idArrsplit.length; i++) {
        if (StringTool.isNull(idArrsplit[i]))
        continue;
      sysDictService.deleteItems(idArrsplit[i], "0", adminService);
    }

    context.setResult("success");

  }

}