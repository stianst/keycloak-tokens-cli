package org.keycloak.cli.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.cli.enums.Flow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class Config {

    @ConfigProperty(name = "kct.context", defaultValue = "default")
    String context;

    @ConfigProperty(name = "kct.issuer")
    String issuer;

    @ConfigProperty(name = "kct.client")
    String clientId;

    @ConfigProperty(name = "kct.client-secret")
    Optional<String> clientSecret;

    @ConfigProperty(name = "kct.user")
    String username;

    @ConfigProperty(name = "kct.user-password")
    String userPassword;

    @ConfigProperty(name = "kct.flow")
    Flow flow;

    @ConfigProperty(name = "kct.scopes")
    List<String> scope;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getIssuer() {
        return get("issuer", String.class);
    }

    public String getClientId() {
        return get("client", String.class);
    }

    public String getClientSecret() {
        return get("client-secret", String.class);
    }

    public String getUsername() {
        return get("user", String.class);
    }

    public String getUserPassword() {
        return get("user-password", String.class);
    }

    public Flow getFlow() {
        return get("flow", Flow.class);
    }

    public List<String> getScope() {
        return Arrays.stream(get("scopes", String.class).split(",")).map(String::trim).collect(Collectors.toList());
    }

    private <T> T get(String key, Class<T> clazz) {
        String k = context.equals("default") ? "kct." + key : "kct." + context + "." + key;
        return ConfigProvider.getConfig().getValue(k, clazz);
    }

}
