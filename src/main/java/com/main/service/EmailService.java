package com.main.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message, String to) {
        boolean f = false;
        String from = "mrmizanbd97@gmail.com";
       //variable for mail
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
       // setting important info to properties object
       //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

       // step : get session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mrmizanbd97@gmail.com", "");
            }

        });
        session.setDebug(true);
        MimeMessage mimeMessage = new MimeMessage(session);
    try {
        mimeMessage.setFrom(from);
        mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
        mimeMessage.setSubject(subject);
//        mimeMessage.setText(message);
        mimeMessage.setContent(message,"html/text");
        Transport.send(mimeMessage);
        System.out.println("msg sent.............");
        f=true;
    } catch (MessagingException e) {
        e.printStackTrace();
    }
        return f;
    }
}
