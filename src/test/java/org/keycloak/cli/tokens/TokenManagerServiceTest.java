package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.Tokens;

import java.time.Instant;
import java.util.Set;

@QuarkusTest
@ExtendWith({ConfigTestProfile.class})
public class TokenManagerServiceTest {

    @Inject
    ConfigService configService;

    @Inject
    TokenManagerService tokens;

    @Inject
    TokenStoreService tokenStoreService;

    @BeforeEach
    public void setContext() {
        configService.setCurrentContext("test-password");
    }

    @AfterEach
    public void resetContext() {
        configService.setCurrentContext("test-service-account");
    }

    @AfterEach
    public void clearTokens() {
        tokenStoreService.clearAll();
    }

    @Test
    public void tokensNotRefreshed() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null, false);
        String access1 = tokens.getToken(TokenType.ACCESS, null, false);

        Assertions.assertNotNull(refresh1);
        Assertions.assertNotNull(access1);

        String refresh2 = tokens.getToken(TokenType.REFRESH, null, false);
        String access2 = tokens.getToken(TokenType.ACCESS, null, false);

        Assertions.assertEquals(refresh1, refresh2);
        Assertions.assertEquals(access1, access2);
    }

    @Test
    public void tokensRefreshed() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null, false);
        String access1 = tokens.getToken(TokenType.ACCESS, null, false);

        Assertions.assertNotNull(refresh1);
        Assertions.assertNotNull(access1);

        Tokens current = tokenStoreService.getCurrent();
        current.setExpiresAt(Instant.now().getEpochSecond());
        tokenStoreService.updateCurrent(current);

        String refresh2 = tokens.getToken(TokenType.REFRESH, null, false);
        String access2 = tokens.getToken(TokenType.ACCESS, null, false);

        Assertions.assertNotEquals(refresh1, refresh2);
        Assertions.assertNotEquals(access1, access2);

        String access3 = tokens.getToken(TokenType.ACCESS, null, false);
        Assertions.assertEquals(access2, access3);

        String access4 = tokens.getToken(TokenType.ACCESS, null, true);
        Assertions.assertNotEquals(access3, access4);
    }

    @Test
    public void invalidRefresh() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null, false);
        String access1 = tokens.getToken(TokenType.ACCESS, null, false);

        Assertions.assertNotNull(refresh1);
        Assertions.assertNotNull(access1);

        Tokens current = tokenStoreService.getCurrent();
        current.setRefreshToken("invalid");
        tokenStoreService.updateCurrent(current);

        String access3 = tokens.getToken(TokenType.ACCESS, null, true);
        Assertions.assertNotNull(access3);
    }

    @Test
    public void retrieveBroaderScopeFromRefresh() {
        tokens.getToken(TokenType.ACCESS, Set.of("email"), false);

        try {
            tokens.getToken(TokenType.ACCESS, Set.of("email,roles"), false);
            Assertions.fail("Expected exception");
        } catch (RuntimeException e) {
            Assertions.assertEquals("Requested scopes must be a subset of configured scopes", e.getMessage());
        }
    }

    @Test
    public void retrieveSmallerScopeFromRefresh() {
        String access1 = tokens.getToken(TokenType.ACCESS, null, false);
        JsonNode jsonNode1 = OpenIDAssertions.assertEncodedToken(access1);

        Assertions.assertNotNull(jsonNode1.get("email"));
        Assertions.assertNotNull(jsonNode1.get("realm_access"));
        Assertions.assertNotNull(jsonNode1.get("resource_access"));

        String access2 = tokens.getToken(TokenType.ACCESS, Set.of("email"), false);
        JsonNode jsonNode2 = OpenIDAssertions.assertEncodedToken(access2);

        Assertions.assertNotNull(jsonNode2.get("email"));
        Assertions.assertNull(jsonNode2.get("realm_access"));
        Assertions.assertNull(jsonNode2.get("resource_access"));
    }

    @Test
    public void revokeRefreshToken() {
        String refresh1 = tokens.getToken(TokenType.REFRESH, null, false);
        OpenIDAssertions.assertEncodedToken(refresh1);

        Tokens stored1 = tokenStoreService.getCurrent();
        Assertions.assertEquals(refresh1, stored1.getRefreshToken());

        Assertions.assertTrue(tokens.revoke(TokenType.REFRESH));

        Assertions.assertNull(tokenStoreService.getCurrent());

        String refresh2 = tokens.getToken(TokenType.REFRESH, null, false);
        OpenIDAssertions.assertEncodedToken(refresh2);

        Assertions.assertNotEquals(refresh1, refresh2);
    }

    @Test
    public void revokeAccessToken() {
        String access1 = tokens.getToken(TokenType.ACCESS, null, false);
        OpenIDAssertions.assertEncodedToken(access1);

        Tokens stored1 = tokenStoreService.getCurrent();
        Assertions.assertEquals(access1, stored1.getAccessToken());

        Assertions.assertTrue(tokens.revoke(TokenType.ACCESS));

        Assertions.assertNull(tokenStoreService.getCurrent().getAccessToken());

        String access2 = tokens.getToken(TokenType.ACCESS, null, false);
        OpenIDAssertions.assertEncodedToken(access2);

        Assertions.assertNotEquals(access1, access2);
    }


}
