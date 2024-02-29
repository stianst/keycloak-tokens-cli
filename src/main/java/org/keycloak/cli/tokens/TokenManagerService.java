package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.TokenService;
import org.keycloak.cli.oidc.Tokens;

import java.util.List;

@ApplicationScoped
public class TokenManagerService {

    @Inject
    TokenService tokenService;

    public String getToken(TokenType tokenType, List<String> scope) {
        Tokens tokens = tokenService.getToken(scope);
        return switch (tokenType) {
            case ID -> tokens.getIdToken();
            case ACCESS -> tokens.getAccessToken();
            case REFRESH -> tokens.getRefreshToken();
        };
    }

}
