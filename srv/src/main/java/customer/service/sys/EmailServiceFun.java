package customer.service.sys;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.MailBody;
import cds.gen.MailJson;
import cds.gen.sys.T11MailTemplate;
import cds.gen.sys.T12Config;
import customer.bean.sys.InnerEmailEntity;
import customer.dao.sys.T11MailTempDao;
import customer.dao.sys.T12ConfigDao;
import javax.mail.*;
import javax.mail.internet.*;

@Component
public class EmailServiceFun {
    @Autowired
    private T12ConfigDao Config;

    @Autowired
    private T11MailTempDao mailTempDao;

    public void sendEmailFun(Collection<MailJson> mailJsons) {
        // 1.Get Mail Server Information
        String to = "";
        String mailPassword = "";
        String mailUser = "";
        String mailHost = "";
        String TemplateID = "";
        String mailFrom = "";

        // Get Mail information
        for (MailJson mailinfo : mailJsons) {
            to = mailinfo.getMailTo();
            TemplateID = mailinfo.getTemplateId();
        }
        List<T12Config> mailConfig = Config.get("MAIL");
        for (T12Config config : mailConfig) {
            if (config.getConCode().equals("MAIL_HOST")) {
                mailHost = config.getConValue();
            }
            if (config.getConCode().equals("MAIL_PASSWORD")) {
                mailPassword = config.getConValue();
            }
            if (config.getConCode().equals("MAIL_USERNAME")) {
                mailUser = config.getConValue();
            }
            if (config.getConCode().equals("MAIL_FROM")) {
                mailFrom = config.getConValue();
            }
        }
        final String user = mailUser;
        final String password = mailPassword;
        // 2.Get Mail Template
        T11MailTemplate o = mailTempDao.getTemplate(TemplateID);
        // 3.replace dynmic content
        Map<String, String> map = new HashMap<>();
        for (MailJson mailinfo : mailJsons) {
            Collection<MailBody> body = mailinfo.getMailBody();
            for (MailBody b : body) {
                map.put(b.getObject(), b.getValue());
            }
        }
        String body = replaceString(o.getMailContent(), map);

        // 4.send email
        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(o.getMailTitle());

            // Set Body
            BodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(body, "text/html;charset=UTF-8");

            // Set Attachment

            //
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textBodyPart);

            // Set Body to message
            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public String replaceString(String Content, Map<String, String> map) {
        String result = Content;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }

}
