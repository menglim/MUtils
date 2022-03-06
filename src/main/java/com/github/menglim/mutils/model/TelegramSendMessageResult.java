package com.github.menglim.mutils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TelegramSendMessageResult {
    @JsonProperty("message_id")
    private Long messageId;

    private TelegramChat chat;

    private TelegramUser from;
    private Long date;

    @JsonProperty("text")
    private String message;
}
