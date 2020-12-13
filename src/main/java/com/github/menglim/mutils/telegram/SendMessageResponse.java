package com.github.menglim.mutils.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendMessageResponse {

    @JsonProperty("ok")
    private boolean success;

    @JsonProperty("result")
    private TelegramResult result;

}

@Data
class TelegramResult {
    @JsonProperty("message_id")
    private Long messageId;

    private TelegramChat chat;

    private TelegramResultFrom from;
    private Long date;

    @JsonProperty("text")
    private String message;

}

@Data
class TelegramResultFrom {
    private Long id;

    @JsonProperty("is_bot")
    private boolean bot;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("username")
    private String username;
}

@Data
class TelegramChat {

    private Long id;

    private String title;

    private String type;

    @JsonProperty("all_members_are_administrators")
    private boolean allMembersAreAdministrators;
}