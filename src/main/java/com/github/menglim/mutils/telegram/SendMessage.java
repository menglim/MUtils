package com.github.menglim.mutils.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendMessage {

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("text")
    private String message;
}
