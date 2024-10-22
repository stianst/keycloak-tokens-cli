package org.keycloak.cli.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.keycloak.cli.oidc.Tokens;

import java.util.HashMap;
import java.util.Map;

public class TokenStore {

    Map<String, Tokens> tokens = new HashMap<>();

    @JsonProperty("provider-metadata")
    Map<String, String> providerMetadata = new HashMap<>();

    public Map<String, Tokens> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Tokens> tokens) {
        this.tokens = tokens;
    }

    public Map<String, String> getProviderMetadata() {
        return providerMetadata;
    }

    public void setProviderMetadata(Map<String, String> providerMetadata) {
        this.providerMetadata = providerMetadata;
    }
}
