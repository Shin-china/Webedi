package customer.handlers.sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.SYS07CheckDATAContext;
import cds.gen.tableservice.SYS07SaveDATAContext;
import cds.gen.tableservice.SysT07ComOpH;
import cds.gen.tableservice.SysT07ComOpH_;
import cds.gen.tableservice.SysT08ComOpD;
import cds.gen.tableservice.T16EmailH;
import cds.gen.tableservice.T16EmailH_;
import cds.gen.tableservice.T17EmailD;
import cds.gen.tableservice.SYS05MailtempAddContext;
import cds.gen.tableservice.SYS05MailtempDelContext;
import cds.gen.tableservice.TableService_;
import customer.bean.sys.Sys005Mail;
import customer.bean.sys.Sys07;
import customer.bean.sys.Sys07List;
import customer.service.sys.Sys07Service;
import customer.service.sys.SysMailService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys07Handler implements EventHandler {
    @Autowired
    private Sys07Service shelfService;

    // @Autowired
    // ResourceBundleMessageSource rbms;

    // check数据
    @On(event = "SYS07_CHECK_DATA")
    public void checkData(SYS07CheckDATAContext context) {
        Sys07List list = JSON.parseObject(context.getJson(), Sys07List.class);
        shelfService.sys07DetailsCheck(list);
        context.setResult(JSON.toJSONString(list));

    }

    // 保存数据
    @On(event = "SYS07_SAVE_DATA")
    public void saveData(SYS07SaveDATAContext context) {
        Sys07List list = JSON.parseObject(context.getJson(), Sys07List.class);
        shelfService.detailsSave(list);
        context.setResult(JSON.toJSONString(list));
    }

    /**
     * 
     * 检查修改后抬头
     * 
     * @param context       传入上下文
     * @param d012MoveActHs 传入画面输入值
     */
    @Before(entity = T16EmailH_.CDS_NAME, event = CqnService.EVENT_CREATE)
    public void beforeCreateT16H(CdsCreateEventContext context, Stream<T16EmailH> t16Hs) {
        t16Hs.forEach(t06 -> {
            shelfService.checkHcode(t06);
            // 检查头
            shelfService.checkItems(t06.getToItems());

        });

    }

    /**
     * 
     * 检查修改后抬头
     * 
     * @param context       传入上下文
     * @param d012MoveActHs 传入画面输入值
     */
    @Before(entity = T16EmailH_.CDS_NAME, event = CqnService.EVENT_UPDATE)
    public void beforeUpdateT17H(CdsUpdateEventContext context, Stream<T16EmailH> t16Hs) {
        t16Hs.forEach(t06 -> {
            shelfService.checkHcode(t06);
            // 检查头
            shelfService.checkItems(t06.getToItems());

        });

    }
}
