package org.keycloak.cli.oidc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.web.UriBuilder;
import org.keycloak.cli.web.WebCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class AuthorizationCodeService {

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Inject
    ProviderMetadata providerMetadata;

    @Inject
    TokenService tokenService;

    @Inject
    WebCallback webCallback;

    public Tokens getToken(Set<String> scope) {
        webCallback.start();

        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        String redirectUri = "http://127.0.0.1:" + webCallback.getPort() + "/callback";

        PKCE pkce = PKCE.create();

        UriBuilder uriBuilder = UriBuilder.create(providerMetadata.getAuthorizationEndpoint())
                .query("response_type", "code")
                .query("client_id", config.getClient())
                .query("redirect_uri", redirectUri)
                .query("state", state)
                .query("nonce", nonce)
                .query("code_challenge", pkce.getCodeChallenge())
                .query("code_challenge_method", PKCE.S256);

        if (!scope.isEmpty()) {
            uriBuilder.query("scope", String.join(" ", scope));
        }

        interact.openUrl(uriBuilder.toURI());

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

        if (!state.equals(returnedState)) {
            throw new RuntimeException("Invalid state parameter returned");
        }

        Map<String, String> additionalGrantParameters = new HashMap<>();
        additionalGrantParameters.put("code", code);
        additionalGrantParameters.put("code_verifier", pkce.getCodeVerifier());
        additionalGrantParameters.put("redirect_uri", redirectUri);

        return new Tokens(tokenService.getQuarkusClient(scope).getTokens(additionalGrantParameters).await().atMost(TokenService.DEFAULT_WAIT), scope, scope);
    }

}
