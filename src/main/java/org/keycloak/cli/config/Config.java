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

    private Map<String, Issuer> issuers;

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

    public Map<String, Issuer> getIssuers() {
        return issuers;
    }

    public void setIssuers(Map<String, Issuer> issuers) {
        this.issuers = issuers;
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
        @JsonProperty("issuer-ref")
        private String issuerRef;
        private Flow flow;

        private String client;

        @JsonProperty("client-ref")
        private String clientRef;

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

        public String getIssuerRef() {
            return issuerRef;
        }

        public void setIssuerRef(String issuerRef) {
            this.issuerRef = issuerRef;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getClientRef() {
            return clientRef;
        }

        public void setClientRef(String clientRef) {
            this.clientRef = clientRef;
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Issuer {

        private String url;

        private Map<String, Client> clients;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Client> getClients() {
            return clients;
        }

        public void setClients(Map<String, Client> clients) {
            this.clients = clients;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {

        private String id;
        private String secret;

        private Flow flow;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Flow getFlow() {
            return flow;
        }

        public void setFlow(Flow flow) {
            this.flow = flow;
        }
    }

}
