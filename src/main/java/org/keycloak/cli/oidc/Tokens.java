package org.keycloak.cli.oidc;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Tokens {

    private String refreshToken;
    private List<String> refreshScope;
    private String accessToken;
    private String idToken;
    private List<String> tokenScope;

    private Long expiresAt;

    public Tokens() {
    }

    public Tokens(TokenResponse tokenResponse, List<String> refreshScope, List<String> tokenScope) {
        this.refreshToken = tokenResponse.getRefreshToken();
        this.refreshScope = refreshScope;
        this.accessToken = tokenResponse.getAccessToken();
        this.idToken = tokenResponse.getIdToken();
        this.tokenScope = tokenScope;
        this.expiresAt = Instant.now().getEpochSecond() + tokenResponse.getExpiresIn();
    }

    public Tokens(io.quarkus.oidc.client.Tokens tokens, List<String> refreshScope, List<String> tokenScope) {
        this.refreshToken = tokens.getRefreshToken();
        this.refreshScope = refreshScope;
        this.accessToken = tokens.getAccessToken();
        this.idToken = tokens.get("id_token");
        this.tokenScope = tokenScope;
        this.expiresAt = tokens.getAccessTokenExpiresAt();
    }

    public Tokens(String refreshToken, List<String> refreshScope, String accessToken, String idToken, List<String> tokenScope, Long expiresAt) {
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

    public List<String> getRefreshScope() {
        return refreshScope;
    }

    public void setRefreshScope(List<String> refreshScope) {
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

    public List<String> getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(List<String> tokenScope) {
        this.tokenScope = tokenScope;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
