package com.copulaapp.messaging.mailer;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class HtmlMailSender {
    private Message mimeMessage;

    public HtmlMailSender(Configuration config) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(config.authenticate()));
        props.put("mail.smtp.starttls.enable", String.valueOf(config.supportTTL()));
        props.put("mail.smtp.host", String.valueOf(config.getHost()));
        props.put("mail.smtp.port", String.valueOf(config.getPort()));

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                config.getUsername(), config.getPassword());
                    }
                });

        this.mimeMessage = new MimeMessage(session);
    }

    public HtmlMailSender(Session session) {
        this.mimeMessage = new MimeMessage(session);
    }

    public static String readHtmlFile(String dir) throws IOException {
        BufferedReader b = new BufferedReader(new FileReader(new File(dir)));
        StringBuilder builder = new StringBuilder();

        String readLine;
        while ((readLine = b.readLine()) != null) builder.append(readLine);
        return builder.toString();
    }

    private String composeMultipleAddress(List<String> address) {
        StringBuilder builder = new StringBuilder();
        for (String a : address) builder.append(a).append(",");

        builder.deleteCharAt(builder.length() - 1);//delete','
        return builder.toString();
    }

    public void sendMessage() throws MessagingException {
        Transport.send(mimeMessage);
    }

    public void setRecipient(List<String> recipients) throws MessagingException {
        mimeMessage.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(composeMultipleAddress(recipients)));
    }

    public void setRecipient(String recipient) throws MessagingException {
        mimeMessage.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(recipient));
    }

    public void setFrom(String sender) throws MessagingException {
        mimeMessage.setFrom(new InternetAddress(sender));
    }

    public void setMessage(String message) throws MessagingException {
        mimeMessage.setContent(message, "text/html");
    }

    public void setSubject(String subject) throws MessagingException {
        mimeMessage.setSubject(subject);
    }
}