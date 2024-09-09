package org.keycloak.cli.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.keycloak.cli.enums.Flow;

import java.util.Map;
import java.util.Set;

@JsonPropertyOrder({"default-context", "store-tokens", "truststore", "issuers", "clients", "contexts"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Config(
        @JsonProperty("default-context")
        String defaultContext,

        @JsonProperty("store-tokens")
        Boolean storeTokens,

        Map<String, Issuer> issuers,

        Truststore truststore) {

    public Issuer findIssuerByUrl(String url) {
        return issuers.values().stream().filter(i -> i.url().equals(url)).findFirst().orElse(null);
    }

    public Context findContext(String contextId) {
        for (Issuer i : issuers().values()) {
            Context context = i.contexts().get(contextId);
            if (context != null) {
                return context;
            }
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Context(
            Flow flow,
            Client client,
            User user,
            Set<String> scope) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Issuer(String url, Map<String, Context> contexts) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Client(@JsonProperty("client-id") String clientId, String secret) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record User(String username, String password) {
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Truststore(String path, String password) {
    }

}
