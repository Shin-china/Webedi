package customer.handlers.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.net.SocketConnector.ExceptionHandler;
import customer.service.sys.EmailServiceFun;
import cds.gen.common.*;

@Component
@ServiceName(Common_.CDS_NAME)
public class EmailSendHandler implements EventHandler {
    @Autowired
    private EmailServiceFun emailServiceFun;

    @On(event = "sendEmail")
    public void sendEmail(SendEmailContext context) {
        emailServiceFun.sendEmailFun();
    }
}
