package org.keycloak.cli.oidc;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;

import java.net.URI;

@ApplicationScoped
public class ExchangeService {

    @Inject
    ProviderMetadata providerMetadata;

    @Inject
    ConfigService config;

    public TokenResponse getExchange(String accessToken, String audience) {
        return QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(providerMetadata.getTokenEndpoint()))
                .build(ExchangeResource.class).exchange(
                        config.getClient(),
                        config.getClientSecret(),
                        "urn:ietf:params:oauth:grant-type:token-exchange",
                        accessToken,
                        audience);
    }

}
