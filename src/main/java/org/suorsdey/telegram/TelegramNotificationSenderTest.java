package org.suorsdey.telegram;

public class TelegramNotificationSenderTest {

    public static void main(String[] args) {
        TelegramNotificationSender sender = new TelegramNotificationSender("743881289:AAFx6Vj-r4ehZj3u-v2j19FyHRxzxdnEMp4");
        try {
            boolean response = sender.send("-334374613", "Ftp Job-atm1012camera has been executed");
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
