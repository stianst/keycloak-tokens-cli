package org.keycloak.cli.oidc;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DeviceAuthorizationService {

    private static final long DEVICE_DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);

    private static final long DEVICE_MAX_WAIT = TimeUnit.MINUTES.toMillis(5);

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Inject
    ProviderMetadata providerMetadata;

    public Tokens getToken(Set<String> scope) {
        if (scope == null) {
            scope = config.getScope();
        }

        DeviceAuthorizationResponse response = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(providerMetadata.getDeviceAuthorizationEndpoint()))
                .build(DeviceAuthorizationResource.class).request(config.getClient(), scope != null ? String.join(",", scope) : null);

        if (response.getVerificationUriComplete() != null) {
            interact.println("Open the following URL to complete:");
            interact.println(response.getVerificationUriComplete());
        } else {
            interact.println("Open the following URL and enter " + response.getDeviceCode() + " to complete:");
            interact.println(response.getVerificationUri());
        }

        long interval = response.getInterval() != null ? TimeUnit.SECONDS.toMillis(response.getInterval()) : DEVICE_DEFAULT_POOL_INTERVAL;
        long stop = System.currentTimeMillis() + DEVICE_MAX_WAIT;

        TokenResource tokenResource = QuarkusRestClientBuilder.newBuilder().baseUri(URI.create(providerMetadata.getTokenEndpoint()))
                .build(TokenResource.class);

        while (System.currentTimeMillis() < stop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }

            try {
                TokenResponse tokenResponse = tokenResource.device("urn:ietf:params:oauth:grant-type:device_code", response.getDeviceCode(), config.getClient());
                return new Tokens(tokenResponse, scope, scope);
            } catch (WebApplicationException e) {
                TokenResponse tokenResponse = e.getResponse().readEntity(TokenResponse.class);
                if (!tokenResponse.getError().equals("authorization_pending")) {
                    throw e;
                }
            }
        }

        throw new RuntimeException("Timed out");
    }


}
