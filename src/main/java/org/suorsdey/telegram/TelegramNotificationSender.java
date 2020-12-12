package org.suorsdey.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.suorsdey.AppUtils;

@Slf4j
public class TelegramNotificationSender {
    private String botToken;
    private String url = "https://api.telegram.org/bot{bot_token}/sendMessage?chat_id={chat_id}&text={message}";

    public TelegramNotificationSender(String botToken) {
        this.botToken = botToken;
    }

    public boolean send(@NonNull String chatId, @NonNull String message) throws Exception {
        if (AppUtils.getInstance().isNull(botToken)) {
            log.info("BotToken is NULL");
            throw new Exception("BotToken is required");
        }
        if (AppUtils.getInstance().isNull(chatId)) {
            log.info("ChatId is NULL");
            throw new Exception("ChatId is required");
        }
        TelegramBot telegramBot = new TelegramBot(botToken);
        SendMessage sendMessage = new SendMessage(chatId, message).parseMode(ParseMode.HTML)
                .disableWebPagePreview(true);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        log.info("Telegram sent message => " + sendResponse.isOk());
        return sendResponse.isOk();
    }
}
