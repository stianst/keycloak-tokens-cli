package org.keycloak.cli.oidc;

import org.keycloak.cli.enums.TokenType;

public class Tokens {

    private final io.quarkus.oidc.client.Tokens tokens;

    public Tokens(io.quarkus.oidc.client.Tokens tokens) {
        this.tokens = tokens;
    }

    public String getToken(TokenType tokenType) {
        return switch (tokenType) {
            case ID -> tokens.get("id_token");
            case ACCESS -> tokens.getAccessToken();
            case REFRESH -> tokens.getRefreshToken();
        };
    }

}
