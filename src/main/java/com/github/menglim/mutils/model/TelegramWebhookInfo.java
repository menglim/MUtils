package com.github.menglim.mutils.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TelegramWebhookInfo {

    private String url;

    @JsonProperty("has_custom_certificate")
    private boolean customCertificate;

    @JsonProperty("pending_update_count")
    private long pendingUpdateCount;

    @JsonProperty("max_connections")
    private int maxConnections;

    @JsonProperty("ip_address")
    private String ipAddress;

}
