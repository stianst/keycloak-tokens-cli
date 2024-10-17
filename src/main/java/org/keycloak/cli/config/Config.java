package org.keycloak.cli.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.keycloak.cli.enums.Flow;

import java.util.Map;
import java.util.Set;

@JsonPropertyOrder({"default-context", "store-tokens", "truststore", "issuers", "clients", "contexts"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Config {

    @JsonProperty("default-context")
    private String defaultContext;

    @JsonProperty("store-tokens")
    private Boolean storeTokens;

    private Map<String, Issuer> issuers;

    private Truststore truststore;

    public Config() {
    }

    public Config(String defaultContext, Boolean storeTokens, Map<String, Issuer> issuers, Truststore truststore) {
        this.defaultContext = defaultContext;
        this.storeTokens = storeTokens;
        this.issuers = issuers;
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

    public Map<String, Issuer> getIssuers() {
        return issuers;
    }

    public Truststore getTruststore() {
        return truststore;
    }

    public Issuer findIssuerByUrl(String url) {
        return issuers.values().stream().filter(i -> i.url.equals(url)).findFirst().orElse(null);
    }

    public Context findContext(String contextId) {
        for (Issuer i : issuers.values()) {
            Context context = i.contexts.get(contextId);
            if (context != null) {
                return context;
            }
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Context {

        private Flow flow;

        private Client client;

        private User user;

        private Set<String> scope;

        public Context() {
        }

        public Context(Flow flow, Client client, User user, Set<String> scope) {
            this.flow = flow;
            this.client = client;
            this.user = user;
            this.scope = scope;
        }

        public Flow getFlow() {
            return flow;
        }

        public Client getClient() {
            return client;
        }

        public User getUser() {
            return user;
        }

        public Set<String> getScope() {
            return scope;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Issuer {

        private String url;

        private Map<String, Context> contexts;

        @JsonProperty("client-registration-context")
        private String clientRegistrationContext;

        public Issuer() {
        }

        public Issuer(String url, Map<String, Context> contexts, String clientRegistrationContext) {
            this.url = url;
            this.contexts = contexts;
            this.clientRegistrationContext = clientRegistrationContext;
        }

        public String getUrl() {
            return url;
        }

        public Map<String, Context> getContexts() {
            return contexts;
        }

        public String getClientRegistrationContext() {
            return clientRegistrationContext;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Client {

        @JsonProperty("client-id")
        private String clientId;

        private String secret;

        @JsonProperty("registration-token")
        private String registrationToken;

        @JsonProperty("registration-url")
        private String registrationUrl;

        public Client() {
        }

        public Client(String clientId, String secret, String registrationToken, String registrationUrl) {
            this.clientId = clientId;
            this.secret = secret;
            this.registrationToken = registrationToken;
            this.registrationUrl = registrationUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public String getSecret() {
            return secret;
        }

        public String getRegistrationToken() {
            return registrationToken;
        }

        public String getRegistrationUrl() {
            return registrationUrl;
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

        public String getPassword() {
            return password;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Truststore {

        private String path;

        private String password;

        public Truststore() {
        }

        public Truststore(String path, String password) {
            this.path = path;
            this.password = password;
        }

        public String getPath() {
            return path;
        }

        public String getPassword() {
            return password;
        }
    }

}
