package customer.handlers.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.CancelDarftContext;
import cds.gen.tableservice.TableService_;
import customer.dao.common.SqlDao;
import customer.service.sys.DarftCommService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class DarftComnHandler implements EventHandler {

  @Autowired
  DarftCommService darftCommService;
  @Autowired
  SqlDao sqlDao;

  // @Autowired
  private DraftService adminService;

  DarftComnHandler(@Qualifier(TableService_.CDS_NAME) DraftService adminService) {
    this.adminService = adminService;
  }

  @On(event = CancelDarftContext.CDS_NAME)
  public void cancelDraft(CancelDarftContext context) {
    //测试用
    //
    // String jsonStr = context.getParms();
    //DarftParam darft = JSON.parseObject(jsonStr, DarftParam.class);

    String[] params = darftCommService.checkDarftParms(context.getParms());
    if (null == params || params.length == 0) {
      context.setResult("error");
    } else {
      if (darftCommService.checkDarftByOhter(params[0], params[1], context.getUserInfo().getName())) {
        darftCommService.deleteDarftHById(params[0], params[1]);
        if (params.length > 2) {
          for (int i = 2; i < params.length; i++) {

            darftCommService.deleteDarftDById(params[0], params[i]);
          }
        }
        context.setResult("success");
      } else {
        context.setResult("error");
      }

    }

  }

}