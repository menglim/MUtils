package com.github.menglim.mutils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TelegramResponse<T> {

    @JsonProperty("ok")
    private boolean success = false;

    @JsonProperty("result")
    private T result;

    @JsonProperty("description")
    private String description;
}