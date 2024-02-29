package org.keycloak.cli.oidc;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tokens {

    private String refreshToken;
    private Set<String> refreshScope;
    private String accessToken;
    private String idToken;
    private Set<String> tokenScope;

    private Long expiresAt;

    public Tokens() {
    }

    public Tokens(TokenResponse tokenResponse, Set<String> refreshScope, Set<String> tokenScope) {
        this.refreshToken = tokenResponse.getRefreshToken();
        this.refreshScope = refreshScope;
        this.accessToken = tokenResponse.getAccessToken();
        this.idToken = tokenResponse.getIdToken();
        this.tokenScope = tokenScope;
        this.expiresAt = Instant.now().getEpochSecond() + tokenResponse.getExpiresIn();
    }

    public Tokens(io.quarkus.oidc.client.Tokens tokens, Set<String> refreshScope, Set<String> tokenScope) {
        this.refreshToken = tokens.getRefreshToken();
        this.refreshScope = refreshScope;
        this.accessToken = tokens.getAccessToken();
        this.idToken = tokens.get("id_token");
        this.tokenScope = tokenScope;
        this.expiresAt = tokens.getAccessTokenExpiresAt();
    }

    public Tokens(String refreshToken, Set<String> refreshScope, String accessToken, String idToken, Set<String> tokenScope, Long expiresAt) {
        this.refreshToken = refreshToken;
        this.refreshScope = refreshScope;
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

    public Set<String> getRefreshScope() {
        return refreshScope;
    }

    public void setRefreshScope(Set<String> refreshScope) {
        this.refreshScope = refreshScope;
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
