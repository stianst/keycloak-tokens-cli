package org.keycloak.cli.oidc;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.keycloak.cli.config.ConfigService;

import java.net.URI;

@ApplicationScoped
public class ProviderMetadataProducer {

    private ProviderMetadata providerMetadata;

    @Produces
    ProviderMetadata providerMetadata(ConfigService config) {
        if (providerMetadata == null) {
            providerMetadata = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(config.getIssuer()))
                    .build(ProviderMetadataResource.class).getProviderMetadata();
        }
        return providerMetadata;
    }

}
