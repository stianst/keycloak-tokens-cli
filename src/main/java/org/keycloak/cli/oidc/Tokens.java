package org.keycloak.cli.oidc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

import java.time.Instant;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tokens {

    private String refreshToken;
    private Set<String> contextScope;
    private String accessToken;
    private String idToken;
    private Set<String> tokenScope;

    private Long expiresAt;

    public Tokens() {
    }

    public Tokens(OIDCTokens oidcTokens) {
        this.refreshToken = oidcTokens.getRefreshToken() != null ? oidcTokens.getRefreshToken().getValue() : null;
        this.accessToken = oidcTokens.getAccessToken() != null ? oidcTokens.getAccessToken().getValue() : null;
        this.idToken = oidcTokens.getIDTokenString();
        this.expiresAt = Instant.now().getEpochSecond() + oidcTokens.getAccessToken().getLifetime();
    }

    public Tokens(String refreshToken, Set<String> refreshScope, String accessToken, String idToken, Set<String> tokenScope, Long expiresAt) {
        this.refreshToken = refreshToken;
        this.contextScope = refreshScope;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.tokenScope = tokenScope;
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Set<String> getContextScope() {
        return contextScope;
    }

    public void setContextScope(Set<String> contextScope) {
        this.contextScope = contextScope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Set<String> getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(Set<String> tokenScope) {
        this.tokenScope = tokenScope;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
