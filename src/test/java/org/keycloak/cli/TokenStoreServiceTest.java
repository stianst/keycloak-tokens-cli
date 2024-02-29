package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.TokenStoreProfile;
import org.keycloak.cli.oidc.Tokens;
import org.keycloak.cli.tokens.TokenStoreService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(TokenStoreProfile.class)
public class TokenStoreServiceTest {

    @Inject
    TokenStoreService tokens;

    @Inject
    ConfigService config;

    @Test
    public void token() throws IOException {
        File tokensFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-tokens.yaml").toFile();
        if (tokensFile.isFile()) {
            tokensFile.delete();
        }

        config.setContext("test-context");

        Tokens token = new Tokens("refresh", List.of("refresh"), "access", "id", List.of("token"), 123456L);

        tokens.updateCurrent(token);
        Assertions.assertTrue(tokensFile.isFile());

        tokens.getAll().clear();

        tokens.init();

        Tokens current = tokens.getCurrent();
        Assertions.assertEquals("refresh", current.getRefreshToken());
        Assertions.assertEquals("access", current.getAccessToken());
        Assertions.assertEquals("id", current.getIdToken());
        Assertions.assertEquals(List.of("refresh"), current.getRefreshScope());
        Assertions.assertEquals(List.of("token"), current.getTokenScope());
        Assertions.assertEquals(123456L, current.getExpiresAt());

        tokens.clearCurrent();

        Assertions.assertNull(tokens.getCurrent());

        tokens.clearAll();
        Assertions.assertFalse(tokensFile.isFile());

        tokensFile.delete();
    }

}
