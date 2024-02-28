package org.keycloak.cli.oidc;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.enums.TokenType;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TokenService {

    private static final Duration DEFAULT_WAIT = Duration.ofMinutes(1);

    @Inject
    Config config;

    @Inject
    OidcClients quarkusClients;

    @Inject
    DeviceAuthorizationService deviceAuthorizationService;

    OidcClient quarkusClient;

    @Inject
    ProviderMetadata providerMetadata;

    public String getToken(TokenType tokenType) {
        return getToken(tokenType, null);
    }

    public String getToken(TokenType tokenType, List<String> scope) {
        return switch (config.getFlow()) {
            case DEVICE -> deviceAuthorizationService.getToken(tokenType, scope);
            case PASSWORD -> new Tokens(getQuarkusClient(scope).getTokens().await().atMost(DEFAULT_WAIT)).getToken(tokenType);
        };
    }

    private OidcClient getQuarkusClient(List<String> scope) {
        if (quarkusClient != null) {
            return quarkusClient;
        }

        OidcClientConfig clientConfig = new OidcClientConfig();
        clientConfig.setDiscoveryEnabled(false);
        clientConfig.setTokenPath(providerMetadata.getTokenEndpoint());
        clientConfig.setId(config.getIssuer());
        clientConfig.setAuthServerUrl(config.getIssuer());
        clientConfig.setClientId(config.getClientId());

        if (scope != null) {
            clientConfig.setScopes(scope);
        } else if (config.getScope() != null) {
            clientConfig.setScopes(config.getScope());
        }

        if (config.getClientSecret() != null) {
            clientConfig.getCredentials().setSecret(config.getClientSecret());
        }

        if (Flow.PASSWORD.equals(config.getFlow())) {
            clientConfig.getGrant().setType(OidcClientConfig.Grant.Type.PASSWORD);

            Map<String, Map<String, String>> grantOptions = Map.of("password", Map.of("username", config.getUsername(), "password", config.getUserPassword()));
            clientConfig.setGrantOptions(grantOptions);
        } else {
            throw new IllegalArgumentException("Unknown flow");
        }

        Uni<OidcClient> client = quarkusClients.newClient(clientConfig);
        return quarkusClient = client.await().atMost(DEFAULT_WAIT);
    }

}
