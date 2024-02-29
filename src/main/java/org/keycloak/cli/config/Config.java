package org.keycloak.cli.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.keycloak.cli.enums.Flow;

import java.util.Map;

@JsonPropertyOrder({"default", "contexts"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Config {

    @JsonProperty("default")
    private String defaultContext;

    @JsonProperty("store-tokens")
    private Boolean storeTokens;

    private Map<String, Context> contexts;

    public String getDefaultContext() {
        return defaultContext;
    }

    public void setDefaultContext(String defaultContext) {
        this.defaultContext = defaultContext;
    }

    public Boolean getStoreTokens() {
        return storeTokens;
    }

    public void setStoreTokens(Boolean storeTokens) {
        this.storeTokens = storeTokens;
    }

    public Map<String, Context> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, Context> contexts) {
        this.contexts = contexts;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Context {

        private String issuer;
        private Flow flow;

        private String client;

        @JsonProperty("client-secret")
        private String clientSecret;

        private String user;

        @JsonProperty("user-password")
        private String userPassword;

        public String scope;

        public Flow getFlow() {
            return flow;
        }

        public void setFlow(Flow flow) {
            this.flow = flow;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

}
