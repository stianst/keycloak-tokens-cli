package org.keycloak.cli;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.MockConfigFile;
import org.keycloak.cli.container.MockTokenStoreFile;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.Tokens;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.tokens.TokenStoreService;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@ExtendWith({MockTokenStoreFile.class, MockConfigFile.class})
@TestProfile(TokenManagerServiceTest.Profile.class)
public class TokenManagerServiceTest {

    @ConfigProperty(name = "keycloak.issuer")
    String issuerUrl;

    @Inject
    TokenManagerService tokens;

    @Inject
    TokenStoreService tokenStoreService;

    @Inject
    ConfigService configService;

    @BeforeEach
    public void updateIssuer() {
        Config config = configService.getConfig();
        Config.Context context = config.getContexts().get("mycontext");
        context.setIssuer(issuerUrl);
        context.setScope("email,roles");
        config.setStoreTokens(true);
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
        tokens.getToken(TokenType.ACCESS, Set.of("email"));

        try {
            tokens.getToken(TokenType.ACCESS, Set.of("email,roles"));
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

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.tokens.file", MockTokenStoreFile.tokensFile.getAbsolutePath(),
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath()
            );
        }

    }

}
