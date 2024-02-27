package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.OpenIDClient;

import java.util.List;

@ApplicationScoped
public class Tokens {

    @Inject
    OpenIDClient openIDClient;

    public String getToken(TokenType tokenType) {
        return openIDClient.getToken(tokenType);
    }

    public String getToken(TokenType tokenType, List<String> scope) {
        return openIDClient.getToken(tokenType, scope);
    }

}
