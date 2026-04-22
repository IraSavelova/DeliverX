package com.deliverx.auth.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TokenType {

    @JsonProperty("AccessToken")
    ACCESS,

    @JsonProperty("Bearer")
    BEARER,

    @JsonProperty("Sender")
    SENDER,

    @JsonProperty("Refresh")
    REFRESH,

    @JsonProperty("ID")
    ID;

}
