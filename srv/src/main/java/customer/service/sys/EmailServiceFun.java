package customer.service.sys;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import cds.gen.sys.T11MailTemplate;
import cds.gen.sys.T12Config;
import customer.dao.sys.T11MailTempDao;
import customer.dao.sys.T12ConfigDao;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

@Component
public class EmailServiceFun {
    @Autowired
    private T12ConfigDao Config;

    @Autowired
    private T11MailTempDao mailTempDao;

    public void sendEmailFun() {
        // 1.Get Mail Server Information
        String mailHost = Config.get("MAIL_HOST").getConValue();
        String mailPassword = Config.get("MAIL_PASSWORD").getConValue();
        String mailUser = Config.get("MAIL_USER").getConValue();

        // 2.Get Mail Template
        T11MailTemplate o = mailTempDao.getTemplate("test");

        // 3.replace dynmic content
        Map<String, String> map = new HashMap<>();
        map.put("recipient", "发发");
        map.put("content", "Hello");
        String body = replaceString(o.getMailContent(), map);

        // 4.send email
        String to = "stanley.shi@sh.shin-china.com";
        String from = "sap@umc.co.jp";
        Properties props = new Properties();
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUser, mailPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(o.getMailTitle());
            message.setText(body);

            Transport.send(message);

            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public String replaceString(String Content, Map<String, String> map) {
        String result = "";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            result = Content.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }

   
}
