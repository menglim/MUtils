package org.suorsdey;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

@Slf4j
@UtilityClass
public class SMTPServerUtils {
    public boolean startService(@NonNull InetAddress inetAddress, int port, @NonNull ReceiveMessageHandler receiveMessageHandler) {
        try {
            SimpleMessageListenerImpl simpleMessageListener = new SimpleMessageListenerImpl(receiveMessageHandler);
            SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(simpleMessageListener));
            smtpServer.setHostName(inetAddress.getHostAddress());
            smtpServer.setPort(port);
            smtpServer.start();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean startService(@NonNull String hostnameOrIpAddress, int port, @NonNull ReceiveMessageHandler receiveMessageHandler) {
        try {
            SimpleMessageListenerImpl simpleMessageListener = new SimpleMessageListenerImpl(receiveMessageHandler);
            SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(simpleMessageListener));
            smtpServer.setHostName(hostnameOrIpAddress);
            smtpServer.setPort(port);
            smtpServer.start();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public interface ReceiveMessageHandler {
        void OnReceiveMessage(String from, String to, MimeMessage message);
    }
}

@Slf4j
class SimpleMessageListenerImpl implements SimpleMessageListener {

    private SMTPServerUtils.ReceiveMessageHandler receiveMessageHandler;

    public SimpleMessageListenerImpl(SMTPServerUtils.ReceiveMessageHandler receiveMessageHandler) {
        this.receiveMessageHandler = receiveMessageHandler;
    }

    @Override
    public boolean accept(String s, String s1) {
        return true;
    }

    @Override
    public void deliver(String from, String to, InputStream inputStream) throws TooMuchDataException, IOException {
        Session session = Session.getDefaultInstance(new Properties());
        try {
            MimeMessage message = new MimeMessage(session, inputStream);
            log.info("From => " + from);
            log.info("To => " + to);
            log.info("Subject => " + message.getSubject());
            System.out.println("Content => " + message.getContent());
            receiveMessageHandler.OnReceiveMessage(from, to, message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}