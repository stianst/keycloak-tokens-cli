package org.keycloak.cli.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.keycloak.cli.enums.Flow;

import java.util.HashMap;
import java.util.Map;

@JsonPropertyOrder({"default-context", "store-tokens", "truststore", "issuers", "clients", "contexts"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Config {

    @JsonProperty("default-context")
    private String defaultContext;

    @JsonProperty("store-tokens")
    private Boolean storeTokens;

    private Map<String, Issuer> issuers = new HashMap<>();

    private Map<String, Context> contexts = new HashMap<>();

    private Truststore truststore;

    public Config() {
    }

    public Config(String defaultContext, Boolean storeTokens, Map<String, Issuer> issuers, Map<String, Context> contexts, Truststore truststore) {
        this.defaultContext = defaultContext;
        this.storeTokens = storeTokens;
        this.issuers = issuers;
        this.contexts = contexts;
        this.truststore = truststore;
    }

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

    public Truststore getTruststore() {
        return truststore;
    }

    public void setTruststore(Truststore truststore) {
        this.truststore = truststore;
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

        private Issuer issuer;
        private Flow flow;

        private Client client;

        private User user;

        public String[] scope;

        public Context() {
        }

        public Context(Issuer issuer, Flow flow, Client client, User user, String[] scope) {
            this.issuer = issuer;
            this.flow = flow;
            this.client = client;
            this.user = user;
            this.scope = scope;
        }

        public Flow getFlow() {
            return flow;
        }

        public void setFlow(Flow flow) {
            this.flow = flow;
        }

        public Issuer getIssuer() {
            return issuer;
        }

        public void setIssuer(Issuer issuer) {
            this.issuer = issuer;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String[] getScope() {
            return scope;
        }

        public void setScope(String[] scope) {
            this.scope = scope;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Issuer {

        private String url;
        private String ref;

        public Issuer() {
        }

        public Issuer(String url, String ref) {
            this.url = url;
            this.ref = ref;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {

        @JsonProperty("client-id")
        private String clientId;
        private String secret;

        public Client() {
        }

        public Client(String clientId, String secret) {
            this.clientId = clientId;
            this.secret = secret;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class User {

        private String username;
        private String password;

        public User() {
        }

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Truststore {

        private String path;

        private String password;

        public Truststore() {
        }

        public Truststore(String password, String path) {
            this.password = password;
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

}
