package customer.handlers.sys;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.MailJson;
import customer.service.sys.EmailServiceFun;
import cds.gen.common.*;

@Component
@ServiceName(Common_.CDS_NAME)
public class EmailSendHandler implements EventHandler {
    @Autowired
    private EmailServiceFun emailServiceFun;

    @On(event = "sendEmail")
    public void sendEmail(SendEmailContext context) {
        Collection<MailJson> mailJsons = context.getEmailJson();
        emailServiceFun.sendEmailFun(mailJsons);

        context.setResult("sucess");
    }

}
