package org.keycloak.cli.oidc;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;

@ApplicationScoped
public class UserInfoService {

    @Inject
    ProviderMetadata providerMetadata;

    public UserInfo getUserInfo(String accessToken) {
        return QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(providerMetadata.getUserinfoEndpoint()))
                .build(UserInfoResource.class).getUserInfo(accessToken);
    }

}
