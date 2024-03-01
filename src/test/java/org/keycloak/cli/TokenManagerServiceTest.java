package org.keycloak.cli;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.MockTokenStoreResource;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.Tokens;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.tokens.TokenStoreService;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@QuarkusTestResource(MockTokenStoreResource.class)
public class TokenManagerServiceTest {

    @ConfigProperty(name = "keycloak.issuer")
    String issuerUrl;

    @Inject
    TokenManagerService tokens;

    @Inject
    TokenStoreService tokenStoreService;

    @Inject
    ConfigService config;

    @PostConstruct
    public void init() {
        config.getConfig().setStoreTokens(true);

        Config.Context context = new Config.Context();
        context.setIssuer(issuerUrl);
        context.setFlow(Flow.PASSWORD);
        context.setClient("test-password");
        context.setUser("test-user");
        context.setUserPassword("test-user-password");
        context.setScope("roles,email");

        config.getConfig().setContexts(Map.of("test-context", context));

        config.setContext("test-context");
    }

    @Test
    public void tokensNotRefreshed() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null);
        String access1 = tokens.getToken(TokenType.ACCESS, null);

        Assertions.assertNotNull(refresh1);
        Assertions.assertNotNull(access1);

        String refresh2 = tokens.getToken(TokenType.REFRESH, null);
        String access2 = tokens.getToken(TokenType.ACCESS, null);

        Assertions.assertEquals(refresh1, refresh2);
        Assertions.assertEquals(access1, access2);
    }

    @Test
    public void tokensRefreshed() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null);
        String access1 = tokens.getToken(TokenType.ACCESS, null);

        Assertions.assertNotNull(refresh1);
        Assertions.assertNotNull(access1);

        Tokens current = tokenStoreService.getCurrent();
        current.setExpiresAt(Instant.now().getEpochSecond());
        tokenStoreService.updateCurrent(current);

        String refresh2 = tokens.getToken(TokenType.REFRESH, null);
        String access2 = tokens.getToken(TokenType.ACCESS, null);

        Assertions.assertNotEquals(refresh1, refresh2);
        Assertions.assertNotEquals(access1, access2);

        tokenStoreService.clearAll();
    }

    @Test
    public void retrieveBroaderScopeFromRefresh() {
        String access1 = tokens.getToken(TokenType.ACCESS, Set.of("email"));

        try {
            String access2 = tokens.getToken(TokenType.ACCESS, Set.of("email,roles"));
            Assertions.fail("Expected exception");
        } catch (RuntimeException e) {
            Assertions.assertEquals("Requested scopes is not a subset of stored refresh scopes", e.getMessage());
        } finally {
            tokenStoreService.clearAll();
        }
    }

    @Test
    public void retrieveSmallerScopeFromRefresh() {
        String access1 = tokens.getToken(TokenType.ACCESS, null);
        JsonNode jsonNode1 = OpenIDAssertions.assertEncodedToken(access1);

        Assertions.assertNotNull(jsonNode1.get("email"));
        Assertions.assertNotNull(jsonNode1.get("realm_access"));
        Assertions.assertNotNull(jsonNode1.get("resource_access"));

        String access2 = tokens.getToken(TokenType.ACCESS, Set.of("email"));
        JsonNode jsonNode2 = OpenIDAssertions.assertEncodedToken(access2);

        Assertions.assertNotNull(jsonNode2.get("email"));
        Assertions.assertNull(jsonNode2.get("realm_access"));
        Assertions.assertNull(jsonNode2.get("resource_access"));

        tokenStoreService.clearAll();
    }

}
