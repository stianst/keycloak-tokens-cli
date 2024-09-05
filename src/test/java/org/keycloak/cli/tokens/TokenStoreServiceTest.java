package org.keycloak.cli.tokens;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.oidc.Tokens;

import java.io.IOException;
import java.util.Set;

@QuarkusTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith({ConfigTestProfile.class})
public class TokenStoreServiceTest {

    @Inject
    TokenStoreService tokens;

    @Inject
    ConfigService config;

    @Test
    public void token() throws IOException {
        Assertions.assertEquals(0, ConfigTestProfile.TOKENS_FILE.length());

        Tokens token = new Tokens("refresh", Set.of("refresh"), "access", "id", Set.of("token"), 123456L);
        tokens.updateCurrent(token);

        TokenStore tokenStore = ConfigTestProfile.loadTokens();
        Assertions.assertNotNull(tokenStore.getTokens().get("test-service-account"));

        tokens.getAll().clear();
        tokens.init();

        Tokens current = tokens.getCurrent();
        Assertions.assertEquals("refresh", current.getRefreshToken());
        Assertions.assertEquals("access", current.getAccessToken());
        Assertions.assertEquals("id", current.getIdToken());
        Assertions.assertEquals(Set.of("refresh"), current.getContextScope());
        Assertions.assertEquals(Set.of("token"), current.getTokenScope());
        Assertions.assertEquals(123456L, current.getExpiresAt());

        tokens.clearCurrent();

        Assertions.assertNull(tokens.getCurrent());
        Assertions.assertEquals(0, ConfigTestProfile.TOKENS_FILE.length());
    }

}
