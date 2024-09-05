package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationRequest;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.device.DeviceCodeGrant;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;
import org.keycloak.cli.interact.InteractService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class DeviceAuthorizationService {

    private static final long DEVICE_MAX_WAIT = TimeUnit.MINUTES.toMillis(5);

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Inject
    OidcService oidcService;

    public Tokens getToken(Set<String> scope) {
        OIDCProviderMetadata providerMetadata = oidcService.providerMetadata();
        Context context = config.getContext();

        DeviceAuthorizationRequest deviceAuthorizationRequest = new DeviceAuthorizationRequest.Builder(context.getClientId())
                .scope(oidcService.toScope(scope))
                .endpointURI(providerMetadata.getDeviceAuthorizationEndpointURI()).build();

        DeviceAuthorizationSuccessResponse response = oidcService.send(deviceAuthorizationRequest.toHTTPRequest(), DeviceAuthorizationSuccessResponse.class);

        if (response.getVerificationURIComplete() != null) {
            interact.println("Open the following URL to complete:");
            interact.println(response.getVerificationURIComplete().toString());
        } else {
            interact.println("Open the following URL and enter " + response.getDeviceCode() + " to complete:");
            interact.println(response.getVerificationURI().toString());
        }

        long interval = TimeUnit.SECONDS.toMillis(response.getInterval());
        long stop = System.currentTimeMillis() + DEVICE_MAX_WAIT;

        while (System.currentTimeMillis() < stop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }

            DeviceCodeGrant grant = new DeviceCodeGrant(response.getDeviceCode());

            try {
                return oidcService.tokenRequest(grant, scope);
            } catch (OidcException e) {
                if (!"authorization_pending".equals(e.getCode())) {
                    throw e;
                }
            }
        }

        throw new RuntimeException("Timed out");
    }


}
