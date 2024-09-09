package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.OidcException;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.oidc.Tokens;

import java.time.Instant;
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

    public String getToken(TokenType tokenType, Set<String> requestScope, boolean forceRefresh) {
        Context context = config.getContext();

        Set<String> contextScope = context.getScope();
        if (requestScope == null) {
            requestScope = contextScope;
        }

        boolean supportsRefresh = !config.getContext().getFlow().equals(Flow.CLIENT);

        if (TokenType.ID.equals(tokenType) && (!contextScope.contains("openid") || !requestScope.contains("openid"))) {
            throw new TokenManagerException("Request 'openid' scope to retrieve an ID token");
        } else if (TokenType.REFRESH.equals(tokenType) && !supportsRefresh) {
            throw new TokenManagerException("Flow ''{0}'' does not support refresh token", context.getFlow().jsonName());
        }

        if (requestScope != null && !scopeContainsAll(contextScope, requestScope)) {
            throw new TokenManagerException("Requested scopes must be a subset of configured scopes");
        }

        if (!context.storeTokens()) {
            return getTokenType(oidcService.token(requestScope), tokenType);
        }

        Tokens storedTokens = tokenStoreService.getCurrent();

        TokenStatus tokenStatus = checkStored(storedTokens, tokenType, requestScope);
        if (TokenStatus.VALID.equals(tokenStatus) && forceRefresh) {
            tokenStatus = TokenStatus.REFRESH;
        }

        Tokens tokens;
        if (TokenStatus.VALID.equals(tokenStatus)) {
            logger.debug("Using stored tokens");
            return getTokenType(storedTokens, tokenType);
        } else if (TokenStatus.REFRESH.equals(tokenStatus) && storedTokens.getRefreshToken() != null) {
            logger.debug("Refreshing stored tokens");
            try {
                tokens = oidcService.refresh(storedTokens.getRefreshToken(), requestScope);
            } catch (OidcException e) {
                logger.debug("Refresh token not valid, getting new tokens");
                tokens = oidcService.token(requestScope);
            }
            tokens.setContextScope(storedTokens.getContextScope());
            tokens.setTokenScope(requestScope);
        } else if (supportsRefresh) {
            logger.debug("Retrieving tokens");
            tokens = oidcService.token(contextScope);
            if (!scopeMatches(contextScope, requestScope)) {
                logger.debug("Requested scope differs from context scope, refreshing");
                tokens = oidcService.refresh(tokens.getRefreshToken(), requestScope);
            }
            tokens.setContextScope(contextScope);
            tokens.setTokenScope(requestScope);
        } else {
            tokens = oidcService.token(requestScope);
            tokens.setContextScope(contextScope);
            tokens.setTokenScope(requestScope);
        }

        logger.debug("Updating stored tokens");
        tokenStoreService.updateCurrent(tokens);

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

    private TokenStatus checkStored(Tokens storedTokens, TokenType tokenType, Set<String> requestedScope) {
        if (storedTokens == null) {
            return TokenStatus.INVALID;
        }

        Set<String> contextScope = config.getContext().getScope();

        if (!scopeMatches(storedTokens.getContextScope(), contextScope)) {
            logger.debugv("Context scope differs from stored context scope");
            return TokenStatus.INVALID;
        }

        if (storedTokens.getExpiresAt() < Instant.now().getEpochSecond() + 30) {
            logger.debugv("Stored token has expired");
            return TokenStatus.REFRESH;
        }

        if (!scopeMatches(storedTokens.getTokenScope(), requestedScope)) {
            logger.debugv("Requested scope differs from stored scope", config.getContextId());
            return TokenStatus.REFRESH;
        }

        if (getTokenType(storedTokens, tokenType) == null) {
            logger.debugv("Requested token type missing from store", config.getContextId());
            return TokenStatus.REFRESH;
        }

        return TokenStatus.VALID;
    }

    private enum TokenStatus {
        VALID,
        REFRESH,
        INVALID
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
