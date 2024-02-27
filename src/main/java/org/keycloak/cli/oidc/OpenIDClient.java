package org.keycloak.cli.oidc;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.enums.TokenType;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OpenIDClient {

    private static final Duration DEFAULT_WAIT = Duration.ofMinutes(1);

    @Inject
    Config config;

    @Inject
    OidcClients quarkusClients;
    OidcClient quarkusClient;

    ProviderMetadata providerMetadata;

    public ProviderMetadata getProviderMetaData() {
        if (providerMetadata == null) {
            providerMetadata = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(config.getIssuer()))
                    .build(OpenIDConfigurationResource.class).getProviderMetadata();
        }
        return providerMetadata;
    }

    public UserInfo getUserInfo(String accessToken) {
        return QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(getProviderMetaData().getUserinfoEndpoint()))
                .build(UserInfoResource.class).getUserInfo(accessToken);
    }

    public String getToken(TokenType tokenType) {
        return getToken(tokenType, null);
    }

    public String getToken(TokenType tokenType, List<String> scope) {
        io.quarkus.oidc.client.Tokens tokens = getQuarkusClient(scope).getTokens().await().atMost(DEFAULT_WAIT);
        return new Tokens(tokens).getToken(tokenType);
    }

    private OidcClient getQuarkusClient(List<String> scope) {
        if (quarkusClient != null) {
            return quarkusClient;
        }

        ProviderMetadata providerMetaData = getProviderMetaData();

        OidcClientConfig clientConfig = new OidcClientConfig();
        clientConfig.setDiscoveryEnabled(false);
        clientConfig.setTokenPath(providerMetaData.getTokenEndpoint());
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
