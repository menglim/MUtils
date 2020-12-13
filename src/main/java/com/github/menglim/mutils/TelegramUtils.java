package com.github.menglim.mutils;

import com.github.menglim.mutils.telegram.SendMessage;
import com.github.menglim.mutils.telegram.SendMessageResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class TelegramUtils {

    public TelegramUtils() {
    }

    private static TelegramUtils instance;

    public static TelegramUtils getInstance() {
        if (instance == null) instance = new TelegramUtils();
        return instance;
    }


    public SendMessageResponse send(@NonNull String botToken, @NonNull String chatId, @NonNull String message) {
        String url = "https://api.telegram.org/bot{bot_token}/sendMessage";
        try {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setMessage(message);

            url = url.replace("{bot_token}", botToken);

            InputStream inputStream = null;
            inputStream = AppUtils.getInstance().post(url, Constants.ContentType.JSON, AppUtils.getInstance().toJsonString(sendMessage));

            String responseString = AppUtils.getInstance().convertStreamToString(inputStream);
            SendMessageResponse response = (SendMessageResponse) AppUtils.getInstance().toObject(responseString, SendMessageResponse.class);
            if (response.isSuccess()) {
                log.info("Telegram Send Message successfully");
            } else {
                log.error("Telegram Send Message failed");
            }
            return response;
        } catch (Exception e) {
            log.error("Telegram Send Message Error because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
//        TelegramBot telegramBot = new TelegramBot(botToken);
//        SendMessage sendMessage = new SendMessage(chatId, message).parseMode(ParseMode.HTML)
//                .disableWebPagePreview(true);
//        SendResponse sendResponse = telegramBot.execute(sendMessage);
//        log.info("Telegram sent message => " + sendResponse.isOk());
//        return sendResponse.isOk();
    }
}
