package org.keycloak.cli.oidc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.web.BasicWebServer;
import org.keycloak.cli.web.HttpRequest;
import org.keycloak.cli.web.MimeType;
import org.keycloak.cli.web.UriBuilder;

import java.io.IOException;
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

    public Tokens getToken(Set<String> scope) {
        BasicWebServer webServer = BasicWebServer.start();

        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        String redirectUri = "http://127.0.0.1:" + webServer.getPort() + "/callback";

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

        HttpRequest callback;
        try {
            callback = waitForCallback(webServer);
            webServer.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (callback.getQueryParams().containsKey("error")) {
            throw new RuntimeException("Authentication request failed: " + callback.getQueryParams().get("error"));
        }

        String code = callback.getQueryParams().get("code");
        String returnedState = callback.getQueryParams().get("state");

        if (!state.equals(returnedState)) {
            throw new RuntimeException("Invalid state parameter returned");
        }

        Map<String, String> additionalGrantParameters = new HashMap<>();
        additionalGrantParameters.put("code", code);
        additionalGrantParameters.put("code_verifier", pkce.getCodeVerifier());
        additionalGrantParameters.put("redirect_uri", redirectUri);

        return new Tokens(tokenService.getQuarkusClient(scope).getTokens(additionalGrantParameters).await().atMost(TokenService.DEFAULT_WAIT), scope, scope);
    }

    private HttpRequest waitForCallback(BasicWebServer webServer) throws IOException {
        while (true) {
            HttpRequest httpRequest = webServer.accept();
            if (httpRequest.getPath().equals("/favicon.ico")) {
                byte[] body = getClass().getResource("/favicon.ico").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.X_ICON);
            } else if (httpRequest.getPath().equals("/callback")) {
                byte[] body = getClass().getResource("/callback.html").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.HTML);
                return httpRequest;
            } else {
                httpRequest.badRequest();
            }
        }
    }

}
