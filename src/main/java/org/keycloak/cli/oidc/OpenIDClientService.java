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
import org.keycloak.cli.interact.InteractService;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class OpenIDClientService {

    private static final long DEVICE_DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);

    private static final long DEVICE_MAX_WAIT = TimeUnit.MINUTES.toMillis(5);


    private static final Duration DEFAULT_WAIT = Duration.ofMinutes(1);

    @Inject
    Config config;

    @Inject
    OidcClients quarkusClients;

    @Inject
    InteractService interact;

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
        if (Flow.DEVICE.equals(config.getFlow())) {
            return device(tokenType);
        } else {
            io.quarkus.oidc.client.Tokens tokens = getQuarkusClient(scope).getTokens().await().atMost(DEFAULT_WAIT);
            return new Tokens(tokens).getToken(tokenType);
        }
    }

    private String device(TokenType tokenType) {
        DeviceAuthorizationResponse response = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(getProviderMetaData().getDeviceAuthorizationEndpoint()))
                .build(DeviceAuthorizationResource.class).request(config.getClientId(), String.join(",", config.getScope()));

        if (response.getVerificationUriComplete() != null) {
            interact.println("Open the following URL to complete:");
            interact.println(response.getVerificationUriComplete());
        } else {
            interact.println("Open the following URL and enter " + response.getDeviceCode() + " to complete:");
            interact.println(response.getVerificationUri());
        }

        long interval = response.getInterval() != null ? TimeUnit.SECONDS.toMillis(response.getInterval()) : DEVICE_DEFAULT_POOL_INTERVAL;
        long stop = System.currentTimeMillis() + DEVICE_MAX_WAIT;

        TokenResource tokenResource = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(getProviderMetaData().getTokenEndpoint()))
                .build(TokenResource.class);

        while (System.currentTimeMillis() < stop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }

            TokenResponse tokenResponse = tokenResource.device("urn:ietf:params:oauth:grant-type:device_code", response.getDeviceCode(), config.getClientId());
            if (tokenResponse.getError() == null || !tokenResponse.getError().equals("authorization_pending")) {
                return new Tokens(tokenResponse).getToken(tokenType);
            }
        }

        return "the-token";
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
