package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.minidev.json.JSONObject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    public static final Duration DEFAULT_WAIT = Duration.ofMinutes(1);

    @Inject
    NimbusApacheHttpClient apacheHttpClient;

    @Inject
    ConfigService config;

    @Inject
    DeviceAuthorizationService deviceAuthorizationService;

    @Inject
    AuthorizationCodeService authorizationCodeService;

    @Inject
    OidcService oidcService;

    Context context;

    @PostConstruct
    public void init() {
        context = config.getContext();
    }

    public Tokens getToken(Set<String> scope) {
        return switch (context.getFlow()) {
            case DEVICE -> deviceAuthorizationService.getToken(scope);
            case CLIENT, PASSWORD -> oidcService.token(scope);
            case BROWSER -> authorizationCodeService.getToken(scope);
        };
    }

    public Tokens refresh(String refreshToken, Set<String> refreshScope, Set<String> requestScope) {
        RefreshToken rt = new RefreshToken(refreshToken);
        AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(rt);

        try {
            Scope scope = new Scope(requestScope.toArray(new String[0]));

            TokenRequest tokenRequest = new TokenRequest(oidcService.providerMetadata().getTokenEndpointURI(), context.getClientAuthentication(), refreshTokenGrant, scope);
            HTTPResponse response = tokenRequest.toHTTPRequest().send(apacheHttpClient);
            JSONObject jsonObject = response.getBodyAsJSONObject();

            return new Tokens(jsonObject.getAsString("refresh_token"), refreshScope, jsonObject.getAsString("access_token"), jsonObject.getAsString("id_token"), requestScope, jsonObject.getAsNumber("expires_at").longValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean revoke(String token) {
        return oidcService.revoke(token);
    }

}
