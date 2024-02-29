package org.keycloak.cli.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Flow {

    @JsonProperty("password")
    PASSWORD,
    @JsonProperty("device")
    DEVICE

}
