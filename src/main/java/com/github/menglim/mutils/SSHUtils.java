package com.github.menglim.mutils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@UtilityClass
public class SSHUtils {
    public void connectSSH(
            @NonNull
                    String hostname,
            @NonNull
                    String user,
            @NonNull
                    String password,
            @NonNull
                    String command,
            int port,
            @NonNull
                    SSHCallbackResult callbackResult
    ) {
        try {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, hostname, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            log.info("SSH Authorize: " + hostname + "/" + user);
            log.info("Command: " + command);
            Channel channel = session.openChannel("exec");
            ChannelExec exec = (ChannelExec) channel;

            exec.setCommand(command);
            channel.setInputStream(null);

            InputStream in = channel.getInputStream();
            InputStream err = ((ChannelExec) channel).getErrStream();

            channel.connect();
            byte[] tmp = new byte[1024];
            StringBuffer outputBuffer = new StringBuffer();
            int exitStatus = -1;

            while (true) {
                // Log not error
                printOutLog(in, tmp, outputBuffer);

                // Log error
                printOutLog(err, tmp, outputBuffer);

                if (channel.isClosed()) {
                    exitStatus = channel.getExitStatus();
                    log.info("exit-status: " + exitStatus);
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (Exception e) {
//                    System.out.println("Hello Error");
                }
            }
            channel.disconnect();
            session.disconnect();
            log.info("Completed");

            if (exitStatus == 0) {
                callbackResult.onSuccessRespond(outputBuffer.toString());
            } else {
                callbackResult.onErrorRespond(exitStatus, outputBuffer.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            callbackResult.onErrorRespond(-1, e.getMessage());
        }
    }

    public void connectSSH(
            @NonNull
                    String hostname,
            @NonNull
                    String user,
            @NonNull
                    String password,
            @NonNull
                    String command,
            @NonNull
                    SSHCallbackResult callbackResult
    ) {
        connectSSH(hostname, user, password, command, 22, callbackResult);
    }

    private void printOutLog(InputStream inputStream, byte[] tmp, StringBuffer outputBuffer) throws IOException {
        while (inputStream.available() > 0) {
            int i = inputStream.read(tmp, 0, 1024);
            if (i < 0) break;
            String myLog = new String(tmp, 0, i);
            String[] outLogArr = myLog.split("\n");

            for (String outLog : outLogArr) {
                log.info(outLog);
                outputBuffer.append(outLog).append("<br/>");
            }
        }
    }

    public interface SSHCallbackResult {
        void onErrorRespond(int errorCode, String output);

        void onSuccessRespond(String output);
    }
}
