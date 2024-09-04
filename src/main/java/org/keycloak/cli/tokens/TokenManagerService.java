package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.OidcService;
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
    OidcService oidcService;

    public String getToken(TokenType tokenType, Set<String> scope, boolean forceRefresh) {
        Context context = config.getContext();
        if (scope == null) {
            scope = context.getScope() != null ? context.getScope() : Collections.emptySet();
        }

        if (TokenType.ID.equals(tokenType) && (scope == null || !scope.contains("openid"))) {
            throw new RuntimeException("Request openid scope to retrieve an ID token");
        }

        Tokens storedTokens = null;
        if (context.storeTokens()) {
            storedTokens = tokenStoreService.getCurrent();
        }

        Tokens tokens = null;
        if (storedTokens != null) {
            tokens = checkStored(storedTokens, tokenType, scope, forceRefresh);
        }

        if (tokens == null) {
            logger.debugv("Fetching tokens for {0}", config.getContextId());
            tokens = oidcService.token(scope);
        }

        if (context.storeTokens()) {
            if (!tokens.equals(storedTokens)) {
                tokenStoreService.updateCurrent(tokens);
            }
        }

        return getTokenType(tokens, tokenType);
    }

    public boolean revoke(TokenType tokenType) {
        Tokens tokens = tokenStoreService.getCurrent();
        String token = getTokenType(tokens, tokenType);
        boolean revoked = oidcService.revoke(token);
        if (revoked) {
            tokenStoreService.clearCurrent(tokenType);
        }
        return revoked;
    }

    public boolean revoke(String token) {
        return oidcService.revoke(token);
    }

    private Tokens checkStored(Tokens storedTokens, TokenType requestedType, Set<String> requestedScope, boolean forceRefresh) {
        if (!scopeContainsAll(storedTokens.getRefreshScope(), requestedScope)) {
            throw new RuntimeException("Requested scopes is not a subset of stored refresh scopes");
        }

        boolean shouldRefresh = forceRefresh;
        if (storedTokens.getExpiresAt() < Instant.now().getEpochSecond() + 30) {
            logger.debugv("Stored token for {0} has expired, refreshing", config.getContextId());
            shouldRefresh = true;
        }

        if (!scopeMatches(storedTokens.getTokenScope(), requestedScope)) {
            logger.debugv("Requested scope differs for {0} from stored scope, refreshing", config.getContextId());
            shouldRefresh = true;
        }

        if (requestedType.equals(TokenType.ACCESS) && storedTokens.getAccessToken() == null) {
            logger.debugv("Missing stored access token for {0}, refreshing", config.getContextId());
            shouldRefresh = true;
        }

        if (requestedType.equals(TokenType.ID) && storedTokens.getIdToken() == null) {
            logger.debugv("Missing stored ID token for {0}, refreshing", config.getContextId());
            shouldRefresh = true;
        }

        if (shouldRefresh) {
            try {
                logger.debugv("Refreshing tokens for {0}", config.getContextId());
                return oidcService.refresh(storedTokens.getRefreshToken(), storedTokens.getRefreshScope(), requestedScope);
            } catch (Exception e) {
                logger.warnv("Refresh token is not valid for {0}", config.getContextId());
                return null;
            }
        } else {
            logger.debugv("Using stored tokens for {0}", config.getContextId());
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
