package org.keycloak.cli.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.cli.enums.Flow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class Config {

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

    public String getIssuer() {
        return issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret.orElse(null);
    }

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public Flow getFlow() {
        return flow;
    }

    public List<String> getScope() {
        return scope;
    }

}
