package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.web.WebCallback;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class AuthorizationCodeService {

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Inject
    OidcService oidcService;

    @Inject
    WebCallback webCallback;

    public Tokens getToken(Set<String> scope) {
        webCallback.start();

        State state = new State();
        CodeVerifier codeVerifier = new CodeVerifier();

        URI redirectUri;
        try {
            redirectUri = new URI("http://127.0.0.1:" + webCallback.getPort() + "/callback");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        AuthorizationRequest authorizationRequest = new AuthorizationRequest.Builder(new ResponseType("code"), config.getContext().getClientId())
                .endpointURI(oidcService.providerMetadata().getAuthorizationEndpointURI())
                .redirectionURI(redirectUri)
                .scope(oidcService.toScope(scope))
                .state(state)
                .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                .build();

        interact.openUrl(authorizationRequest.toURI());

        Map<String, String> callback;
        try {
            callback = webCallback.getCallback();
            webCallback.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (callback.containsKey("error")) {
            throw new RuntimeException("Authentication request failed: " + callback.get("error"));
        }

        String code = callback.get("code");
        String returnedState = callback.get("state");

        if (!state.getValue().equals(returnedState)) {
            throw new RuntimeException("Invalid state parameter returned");
        }

        try {
            return oidcService.tokenRequest(new AuthorizationCodeGrant(new AuthorizationCode(code), redirectUri, codeVerifier), scope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
