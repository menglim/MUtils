package com.github.menglim.mutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.menglim.mutils.model.TelegramResponse;
import com.github.menglim.mutils.model.TelegramSendMessageResult;
import com.github.menglim.mutils.model.TelegramWebhookInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

@Slf4j
public class TelegramUtils {

    public TelegramUtils() {
    }

    private static TelegramUtils instance;

    public static TelegramUtils getInstance() {
        if (instance == null) instance = new TelegramUtils();
        return instance;
    }

    public TelegramResponse<TelegramSendMessageResult> send(@NonNull String botToken, @NonNull String chatId, @NonNull String message) {
        String url = "https://api.telegram.org/bot{bot_token}/sendMessage";
        try {
            url = url.replace("{bot_token}", botToken);

            TelegramResponse<TelegramSendMessageResult> response = null;
            if (message.length() > 4096) {
                for (int i = 0; i < message.length(); i = i + 4096) {
                    HashMap<String, Object> body = new HashMap<>();
                    body.put("chat_id", chatId);
                    body.put("text", StringUtils.substring(message, i, i + 4096));
                    response = AppUtils.getInstance().postJson(url, body,
                            new TypeReference<TelegramResponse<TelegramSendMessageResult>>() {
                            }
                    );
                }
            } else {
                HashMap<String, Object> body = new HashMap<>();
                body.put("chat_id", chatId);
                body.put("text", message);
                response = AppUtils.getInstance().postJson(url, body,
                        new TypeReference<TelegramResponse<TelegramSendMessageResult>>() {
                        }
                );
            }
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
    }

    public TelegramResponse<Boolean> deleteWebhook(@NonNull String botToken) {
        String url = "https://api.telegram.org/bot{bot_token}/deleteWebhook";
        try {
            url = url.replace("{bot_token}", botToken);
            TelegramResponse<Boolean> response = AppUtils.getInstance().getJson(url,
                    new TypeReference<TelegramResponse<Boolean>>() {
                    }
            );
            if (response.isSuccess()) {
                log.info("DeleteWebhook successfully");
            } else {
                log.error("DeleteWebhook failed");
            }
            return response;
        } catch (Exception e) {
            log.error("DeleteWebhook error because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public TelegramResponse<TelegramWebhookInfo> getWebhookInfo(@NonNull String botToken) {
        String url = "https://api.telegram.org/bot{bot_token}/getWebhookInfo";
        try {
            url = url.replace("{bot_token}", botToken);
            TelegramResponse<TelegramWebhookInfo> response = AppUtils.getInstance().getJson(url,
                    new TypeReference<TelegramResponse<TelegramWebhookInfo>>() {
                    }
            );
            return response;
        } catch (Exception e) {
            log.error("DeleteWebhook error because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public TelegramResponse<Boolean> setWebhook(@NonNull String botToken, String webhookUrl) {
        String url = "https://api.telegram.org/bot{bot_token}/setWebhook?url=" + webhookUrl;
        try {
            url = url.replace("{bot_token}", botToken);
            TelegramResponse<Boolean> response = AppUtils.getInstance().getJson(url,
                    new TypeReference<TelegramResponse<Boolean>>() {
                    }
            );
            if (response.isSuccess()) {
                log.info("SetWebhook successfully with " + webhookUrl);
            } else {
                log.error("SetWebhook failed");
            }
            return response;
        } catch (Exception e) {
            log.error("SetWebhook error because " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
