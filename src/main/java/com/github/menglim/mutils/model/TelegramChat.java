package com.github.menglim.mutils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TelegramChat {

    private Long id;

    private String title;

    private String type;

    @JsonProperty("all_members_are_administrators")
    private boolean allMembersAreAdministrators;
}
