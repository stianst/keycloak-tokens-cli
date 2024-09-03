package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationRequest;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.device.DeviceCodeGrant;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
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

    private static final long DEVICE_DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);

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
                .scope(!scope.isEmpty() ? new Scope(scope.toArray(new String[0])) : null)
                .endpointURI(providerMetadata.getDeviceAuthorizationEndpointURI()).build();

        try {
            HTTPResponse httpResponse = deviceAuthorizationRequest.toHTTPRequest().send(oidcService.httpClient);
            DeviceAuthorizationSuccessResponse response = DeviceAuthorizationResponse.parse(httpResponse).toSuccessResponse();

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

                ClientAuthentication clientAuthentication = context.getClientAuthentication();
                Scope s = new Scope(scope.toArray(new String[0]));
                TokenRequest tokenRequest;
                if (clientAuthentication != null) {
                    tokenRequest = new TokenRequest(providerMetadata.getTokenEndpointURI(), context.getClientAuthentication(), grant, s);
                } else {
                    tokenRequest = new TokenRequest(providerMetadata.getTokenEndpointURI(), context.getClientId(), grant, s);
                }
                OIDCTokenResponse tokenResponse = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send(oidcService.httpClient));
                if (tokenResponse.indicatesSuccess()) {
                    return new Tokens(tokenResponse.toSuccessResponse(), scope, scope);
                } else if (!tokenResponse.toErrorResponse().getErrorObject().getCode().equals("authorization_pending")) {
                    throw new RuntimeException(tokenResponse.toErrorResponse().getErrorObject().getDescription());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Timed out");
    }


}
