package org.keycloak.cli.tokens;

import org.keycloak.cli.oidc.Tokens;

import java.util.HashMap;
import java.util.Map;

public class TokenStore {

    Map<String, Tokens> tokens = new HashMap<>();

    public Map<String, Tokens> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Tokens> tokens) {
        this.tokens = tokens;
    }

}
