package org.keycloak.cli.oidc;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.Flow;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TokenService {

    private static final Duration DEFAULT_WAIT = Duration.ofMinutes(1);

    @Inject
    ConfigService config;

    @Inject
    OidcClients quarkusClients;

    @Inject
    DeviceAuthorizationService deviceAuthorizationService;

    OidcClient quarkusClient;

    @Inject
    ProviderMetadata providerMetadata;

    public Tokens getToken(List<String> scope) {
        return switch (config.getFlow()) {
            case DEVICE -> deviceAuthorizationService.getToken(scope);
            case PASSWORD -> new Tokens(getQuarkusClient(scope).getTokens().await().atMost(DEFAULT_WAIT), scope, scope);
        };
    }

    public Tokens refresh(String refreshToken, List<String> refreshScope, List<String> requestScope) {
        return new Tokens(getQuarkusClient(requestScope).refreshTokens(refreshToken).await().atMost(DEFAULT_WAIT), refreshScope, requestScope);
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
        clientConfig.setClientId(config.getClient());

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

            Map<String, Map<String, String>> grantOptions = Map.of("password", Map.of("username", config.getUser(), "password", config.getUserPassword()));
            clientConfig.setGrantOptions(grantOptions);
        } else {
            throw new IllegalArgumentException("Unknown flow");
        }

        Uni<OidcClient> client = quarkusClients.newClient(clientConfig);
        return quarkusClient = client.await().atMost(DEFAULT_WAIT);
    }

}
