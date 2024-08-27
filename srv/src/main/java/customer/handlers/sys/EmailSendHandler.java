package customer.handlers.sys;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.modeler.ParameterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.changeset.ChangeSetContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.MailJson;
import ch.qos.logback.core.encoder.JsonEscapeUtil;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.net.SocketConnector.ExceptionHandler;
import customer.bean.sys.EmailEntity;
import customer.service.sys.EmailServiceFun;
import cds.gen.MailJson;
import com.alibaba.fastjson.TypeReference;
import cds.gen.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;

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
