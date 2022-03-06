package com.github.menglim.mutils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TelegramUser {
    private Long id;

    @JsonProperty("is_bot")
    private boolean bot;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("username")
    private String username;
}
