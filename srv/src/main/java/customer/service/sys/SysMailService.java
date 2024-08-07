
package customer.service.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.messages.Messages;

import cds.gen.sys.T11MailTemplate;
import customer.bean.sys.Sys005Mail;
import customer.dao.sys.MailTempDao;

@Component
public class SysMailService {
    @Autowired
    private MailTempDao mailTempDao;

    @Autowired
    Messages messages;

    public void addMailTemp(Sys005Mail mail) {
        //
        T11MailTemplate O = T11MailTemplate.create();
        O.setTemplateId(mail.getTEMPLATE_ID());
        O.setMailName(mail.getMAIL_NAME());
        O.setMailTitle(mail.getMAIL_TITLE());
        O.setMailContent(mail.getMAIL_CONTENT());
        mailTempDao.Insert(O);
    }

    public void updateMailTemp(Sys005Mail mail) {
        //
        T11MailTemplate O = T11MailTemplate.create();
        O.setTemplateId(mail.getTEMPLATE_ID());
        O.setMailName(mail.getMAIL_NAME());
        O.setMailTitle(mail.getMAIL_TITLE());
        O.setMailContent(mail.getMAIL_CONTENT());
        mailTempDao.Update(O);

    }

    public void deleteMailTemp(String templateId) {
        //
        mailTempDao.Delete(templateId);

    }

    public boolean tempIsExist(String id) {
        // 判断是否存在
        return mailTempDao.mailTemplateExist(id);
    }

}