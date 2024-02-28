package org.keycloak.cli.oidc;

import org.keycloak.cli.enums.TokenType;

public class Tokens {

    private io.quarkus.oidc.client.Tokens tokens;
    private TokenResponse tokenResponse;

    public Tokens(io.quarkus.oidc.client.Tokens tokens) {
        this.tokens = tokens;
    }

    public Tokens(TokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    public String getToken(TokenType tokenType) {
        if (tokens != null) {
            return switch (tokenType) {
                case ID -> tokens.get("id_token");
                case ACCESS -> tokens.getAccessToken();
                case REFRESH -> tokens.getRefreshToken();
            };
        } else {
            return switch (tokenType) {
                case ID -> tokenResponse.getIdToken();
                case ACCESS -> tokenResponse.getAccessToken();
                case REFRESH -> tokenResponse.getRefreshToken();
            };
        }
    }

}
