package org.keycloak.cli.tokens;

import org.keycloak.cli.oidc.Tokens;

import java.util.HashMap;
import java.util.Map;

public class TokenStore {

    Map<String, Tokens> tokens = new HashMap<>();

    public Map<String, Tokens> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Tokens> tokens) {
        this.tokens = tokens;
    }

//    class Tokens {
//
//        @JsonProperty("refresh_token")
//        private String refreshToken;
//        @JsonProperty("refresh_scope")
//        private String refreshScope;
//        @JsonProperty("id_token")
//        private String idToken;
//        @JsonProperty("access_token")
//        private String accessToken;
//        @JsonProperty("token_scope")
//        private String tokenScope;
//
//        public String getRefreshToken() {
//            return refreshToken;
//        }
//
//        public void setRefreshToken(String refreshToken) {
//            this.refreshToken = refreshToken;
//        }
//
//        public String getRefreshScope() {
//            return refreshScope;
//        }
//
//        public void setRefreshScope(String refreshScope) {
//            this.refreshScope = refreshScope;
//        }
//
//        public String getIdToken() {
//            return idToken;
//        }
//
//        public void setIdToken(String idToken) {
//            this.idToken = idToken;
//        }
//
//        public String getAccessToken() {
//            return accessToken;
//        }
//
//        public void setAccessToken(String accessToken) {
//            this.accessToken = accessToken;
//        }
//
//        public String getTokenScope() {
//            return tokenScope;
//        }
//
//        public void setTokenScope(String tokenScope) {
//            this.tokenScope = tokenScope;
//        }
//    }

}
