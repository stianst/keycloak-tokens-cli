package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.TokenService;
import org.keycloak.cli.oidc.Tokens;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@ApplicationScoped
public class TokenManagerService {

    private static final Logger logger = Logger.getLogger(TokenManagerService.class);

    @Inject
    ConfigService config;

    @Inject
    TokenStoreService tokenStoreService;

    @Inject
    TokenService tokenService;

    public String getToken(TokenType tokenType, Set<String> scope) {
        if (scope == null) {
            scope = config.getScope() != null ? config.getScope() : Collections.emptySet();
        }

        if (TokenType.ID.equals(tokenType) && (scope == null || !scope.contains("openid"))) {
            throw new RuntimeException("Request openid scope to retrieve an ID token");
        }

        Tokens storedTokens = null;
        if (config.isStoreTokens()) {
            storedTokens = tokenStoreService.getCurrent();
        }

        Tokens tokens;
        if (storedTokens != null) {
            tokens = checkStored(storedTokens, tokenType, scope);
        } else {
            tokens = tokenService.getToken(scope);
        }

        if (config.isStoreTokens()) {
            if (!tokens.equals(storedTokens)) {
                tokenStoreService.updateCurrent(tokens);
            }
        }

        return getTokenType(tokens, tokenType);
    }

    public boolean revoke(TokenType tokenType) {
        Tokens tokens = tokenStoreService.getCurrent();
        String token = getTokenType(tokens, tokenType);
        boolean revoked = tokenService.revoke(token);
        if (revoked) {
            tokenStoreService.clearCurrent(tokenType);
        }
        return revoked;
    }

    private Tokens checkStored(Tokens storedTokens, TokenType requestedType, Set<String> requestedScope) {
        if (!scopeContainsAll(storedTokens.getRefreshScope(), requestedScope)) {
            throw new RuntimeException("Requested scopes is not a subset of stored refresh scopes");
        }

        boolean shouldRefresh = false;
        if (storedTokens.getExpiresAt() < Instant.now().getEpochSecond() + 30) {
            logger.debug("Stored token has expired, refreshing");
            shouldRefresh = true;
        }

        if (!scopeMatches(storedTokens.getTokenScope(), requestedScope)) {
            logger.debug("Requested scope differs from stored scope, refreshing");
            shouldRefresh = true;
        }

        if (requestedType.equals(TokenType.ACCESS) && storedTokens.getAccessToken() == null) {
            shouldRefresh = true;
        }

        if (requestedType.equals(TokenType.ID) && storedTokens.getIdToken() == null) {
            shouldRefresh = true;
        }

        if (shouldRefresh) {
            return tokenService.refresh(storedTokens.getRefreshToken(), storedTokens.getRefreshScope(), requestedScope);
        } else {
            logger.debug("Using stored tokens");
            return storedTokens;
        }
    }

    private String getTokenType(Tokens tokens, TokenType tokenType) {
        return switch (tokenType) {
            case ID -> tokens.getIdToken();
            case ACCESS -> tokens.getAccessToken();
            case REFRESH -> tokens.getRefreshToken();
        };
    }

    private boolean scopeContainsAll(Set<String> refreshScope, Set<String> requestedScope) {
        if (requestedScope == null) {
            return true;
        }

        if (refreshScope == null) {
            return requestedScope.isEmpty();
        }

        return refreshScope.containsAll(requestedScope);
    }

    private boolean scopeMatches(Set<String> storedScope, Set<String> requestedScope) {
        if (requestedScope == null) {
            return storedScope == null || storedScope.isEmpty();
        }

        if (storedScope == null) {
            return requestedScope.isEmpty();
        }

        return storedScope.equals(requestedScope);
    }

}
