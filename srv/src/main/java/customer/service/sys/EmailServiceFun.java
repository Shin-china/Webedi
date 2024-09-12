package customer.service.sys;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Base64;

import javax.mail.util.ByteArrayDataSource;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.MailBody;
import cds.gen.MailJson;
import cds.gen.sys.T11MailTemplate;
import cds.gen.sys.T12Config;
import customer.dao.sys.T11MailTempDao;
import customer.dao.sys.T12ConfigDao;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

@Component
public class EmailServiceFun {
    @Autowired
    private T12ConfigDao Config;

    @Autowired
    private T11MailTempDao mailTempDao;

    public void sendEmailFun(Collection<MailJson> mailJsons) throws IOException {
        // 1.Get Mail Server Information
        String to = "";
        String mailPassword = "";
        String mailUser = "";
        String mailHost = "";
        String TemplateID = "";
        String mailFrom = "";
        String filecontent = "";
        String filename = "";

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
                // file name
                if (b.getObject().toString().equals("filename")) {
                    filename = b.getValue();
                }
                if (b.getObject().toString().equals("filecontent")) {
                    filecontent = b.getValue();
                }

                // file content
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
            BodyPart filBodyPart = new MimeBodyPart();
            InputStream is = base2InputStream(filecontent);
            DataSource source = new ByteArrayDataSource(is, "application/pdf");
            filBodyPart.setDataHandler(new DataHandler(source));
            filBodyPart.setFileName(filename);

            //
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textBodyPart);
            if (source != null) {
                multipart.addBodyPart(filBodyPart);
            }

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

    public InputStream base2InputStream(String base64String) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64String);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

}
