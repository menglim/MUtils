package com.github.menglim.mutils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Slf4j
@UtilityClass
public class EmailUtils {

    public boolean sendEmail(
            String smtpHost,
            int port,
            boolean authenticationEnable,
            SendEmailSecurityOption sendEmailSecurityOption,
            String username,
            String password,
            @NonNull String sendFrom,
            @NonNull String toCommaOption,
            String ccCommaOption,
            @NonNull String subject,
            @NonNull String message,
            List<String> attachmentRelativePath
    ) {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpHost);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", authenticationEnable);
        switch (sendEmailSecurityOption) {
            case SSL:
                prop.put("mail.smtp.ssl.enable", "true");
                break;
            case TTSL:
                prop.put("mail.smtp.starttls.enable", "true");
                break;
            case None:
            default:
                break;
        }
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Message msg = new MimeMessage(session);
        try {

            msg.setFrom(new InternetAddress(sendFrom));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toCommaOption, false));
            if (AppUtils.getInstance().nonNull(ccCommaOption)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccCommaOption, false));
            }
            msg.setSubject(subject);
            MimeBodyPart text = new MimeBodyPart();
            text.setDataHandler(new DataHandler(new HTMLDataSource(message)));

            List<MimeBodyPart> attachments = null;
            if (AppUtils.getInstance().nonNull(attachmentRelativePath)) {
                for (int i = 0; i < attachmentRelativePath.size(); i++) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(attachmentRelativePath.get(i));
                    try {
                        attachment.setDataHandler(new DataHandler(fileDataSource));
                        attachment.setFileName(fileDataSource.getName());
                        if (attachments == null) attachments = new ArrayList<>();
                        attachments.add(attachment);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(text);
            if (attachments != null) {
                if (attachments.size() > 0) {
                    attachments.forEach(mimeBodyPart -> {
                        try {
                            multipart.addBodyPart(mimeBodyPart);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            msg.setContent(multipart);
            msg.setSentDate(new Date());
            Transport.send(msg);
            log.info("Email has been sent successfully");
            return true;
        } catch (MessagingException e) {
            log.error("Email sending failed because => " + e.getLocalizedMessage());
            log.error(e.getMessage(), e.fillInStackTrace());
            e.printStackTrace();
            return false;
        }
    }


//    /**
//     * @param smtpHost
//     * @param smtpPort
//     * @param smtpUsername           Email address
//     * @param smtpPassword
//     * @param emailFrom              if NULL, replaced by smtpUsername
//     * @param to                     can be separate with coma or not
//     * @param subject
//     * @param message
//     * @param attachmentRelativePath /root/location/of/file
//     * @return true means sending success, false means sending failed. NULL if no attachment
//     */
//
//    public boolean sendEmail(
//            @NonNull String smtpHost,
//            int smtpPort,
//            boolean authenticationEnable,
//            @NonNull String smtpUsername,
//            @NonNull String smtpPassword,
//            boolean enableSSL,
//            String emailFrom,
//            @NonNull String to,
//            String cc,
//            @NonNull String subject,
//            @NonNull String message,
//            List<String> attachmentRelativePath
//    ) {
//        Properties prop = System.getProperties();
//        prop.put("mail.smtp.host", smtpHost);
//        prop.put("mail.smtp.auth", authenticationEnable);
//        prop.put("mail.smtp.port", smtpPort);
//        prop.put("mail.smtp.ssl.enable", enableSSL);
//        Session session = Session.getInstance(prop, null);
//        Message msg = new MimeMessage(session);
//        try {
//
//            if (AppUtils.getInstance().isNull(emailFrom)) emailFrom = smtpUsername;
//            msg.setFrom(new InternetAddress(emailFrom));
//
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
//            if (AppUtils.getInstance().nonNull(cc)) {
//                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
//            }
//            msg.setSubject(subject);
//            MimeBodyPart text = new MimeBodyPart();
//            text.setDataHandler(new DataHandler(new HTMLDataSource(message)));
//
//            List<MimeBodyPart> attachments = null;
//            if (AppUtils.getInstance().nonNull(attachmentRelativePath)) {
//                for (int i = 0; i < attachmentRelativePath.size(); i++) {
//                    MimeBodyPart attachment = new MimeBodyPart();
//                    FileDataSource fileDataSource = new FileDataSource(attachmentRelativePath.get(i));
//                    try {
//                        attachment.setDataHandler(new DataHandler(fileDataSource));
//                        attachment.setFileName(fileDataSource.getName());
//                        if (attachments == null) attachments = new ArrayList<>();
//                        attachments.add(attachment);
//                    } catch (MessagingException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(text);
//            if (attachments != null) {
//                if (attachments.size() > 0) {
//                    attachments.forEach(mimeBodyPart -> {
//                        try {
//                            multipart.addBodyPart(mimeBodyPart);
//                        } catch (MessagingException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//            }
//            msg.setContent(multipart);
//            msg.setSentDate(new Date());
//            SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
//            log.info("Connecting to SMTP Host => " + smtpHost);
//            transport.connect(smtpHost, smtpPort, smtpUsername, smtpPassword);
//            if (transport.isConnected()) {
//                log.info("Connected SMTP Host. Trying to send email now.");
//                transport.sendMessage(msg, msg.getAllRecipients());
//                transport.close();
//                log.info("Email has been sent successfully");
//                return true;
//            } else {
//                log.error("Cannot connect SMTP. Make sure your credential is correct.");
//                return false;
//            }
//        } catch (MessagingException e) {
//            log.error("Email sending failed because => " + e.getLocalizedMessage());
//            log.error(e.getMessage(), e.fillInStackTrace());
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean sendEmail(
            Properties prop,
            String username,
            String password,
            String emailFrom,
            @NonNull String to,
            String cc,
            @NonNull String subject,
            @NonNull String message,
            List<String> attachmentRelativePath
    ) {
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        Message msg = new MimeMessage(session);
        try {

            msg.setFrom(new InternetAddress(emailFrom));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            if (AppUtils.getInstance().nonNull(cc)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }
            msg.setSubject(subject);
            MimeBodyPart text = new MimeBodyPart();
            text.setDataHandler(new DataHandler(new HTMLDataSource(message)));

            List<MimeBodyPart> attachments = null;
            if (AppUtils.getInstance().nonNull(attachmentRelativePath)) {
                for (int i = 0; i < attachmentRelativePath.size(); i++) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(attachmentRelativePath.get(i));
                    try {
                        attachment.setDataHandler(new DataHandler(fileDataSource));
                        attachment.setFileName(fileDataSource.getName());
                        if (attachments == null) attachments = new ArrayList<>();
                        attachments.add(attachment);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(text);
            if (attachments != null) {
                if (attachments.size() > 0) {
                    attachments.forEach(mimeBodyPart -> {
                        try {
                            multipart.addBodyPart(mimeBodyPart);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            msg.setContent(multipart);
            msg.setSentDate(new Date());
            Transport.send(msg);
            log.info("Email has been sent successfully");
            return true;
        } catch (MessagingException e) {
            log.error("Email sending failed because => " + e.getLocalizedMessage());
            log.error(e.getMessage(), e.fillInStackTrace());
            e.printStackTrace();
            return false;
        }
    }

    static class HTMLDataSource implements DataSource {

        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (html == null) throw new IOException("html message is null!");
            return new ByteArrayInputStream(html.getBytes());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public String getName() {
            return "HTMLDataSource";
        }
    }

    public String getRemoteClientIp(MimeMessage message) throws Exception {
        if (message == null) throw new Exception("MimeMessage Object cannot be NULL");
        String[] header = message.getHeader("Received");
        String remoteHost = "";
        if (header.length >= 1) {
            log.info(header[0]);
            remoteHost = header[0];
            remoteHost = remoteHost.replace("(", "");
            remoteHost = remoteHost.replace(")", "");
            remoteHost = remoteHost.replace("[", "");
            remoteHost = remoteHost.replace("]", "");
            String[] tmp = remoteHost.split("\r\n");

            // get client host IP
            String[] tmpSpacing = tmp[0].split(" ");
            return tmpSpacing[tmpSpacing.length - 1];
        }
        return "";
    }
}
