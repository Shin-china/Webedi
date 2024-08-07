package customer.handlers.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.SYS05MailtempAddContext;
import cds.gen.tableservice.TableService_;
import customer.bean.sys.Sys005Mail;
import customer.service.sys.SysMailService;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Sys05Handler {
    @Autowired
    SysMailService sysMailService;

    // 新增邮箱模板
    @On(event = "SYS05_MAILTEMP_add")
    public void addMailTemp(SYS05MailtempAddContext context) {
        String content = context.getUserJson();
        Sys005Mail mail = JSON.parseObject(content, Sys005Mail.class);
        if (sysMailService.tempIsExist(mail.getTEMPLATE_ID())) {
            sysMailService.updateMailTemp(mail);
        } else {
            sysMailService.addMailTemp(mail);
        }

        context.setResult("Success");
    }

    // 删除邮箱模板
    @On(event = "SYS05_MAILTEMP_delete")
    public void deleteMailTemp(SYS05MailtempAddContext context) {
        String content = context.getUserJson();
        Sys005Mail mail = JSON.parseObject(content, Sys005Mail.class);
        sysMailService.deleteMailTemp(mail.getTEMPLATE_ID());
        context.setResult("Success");

    }

}
