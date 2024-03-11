package org.keycloak.cli.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Flow {

    @JsonProperty("password")
    PASSWORD,
    @JsonProperty("device")
    DEVICE,
    @JsonProperty("client")
    CLIENT;

    public String jsonName() {
        try {
            JsonProperty annotation = getClass().getField(name()).getAnnotation(JsonProperty.class);
            return annotation != null ? annotation.value() : name();
        } catch (Throwable t) {
            return name();
        }
    }

}
