package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.MockTokenStoreFile;
import org.keycloak.cli.oidc.Tokens;
import org.keycloak.cli.tokens.TokenStoreService;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@ExtendWith(MockTokenStoreFile.class)
@TestProfile(TokenStoreServiceTest.Profile.class)
public class TokenStoreServiceTest {

    @Inject
    TokenStoreService tokens;

    @Inject
    ConfigService config;

    @ConfigProperty(name = "kct.tokens.file")
    File tokensFile;

    @Test
    public void token() throws IOException {
        config.setContext("test-context");

        Tokens token = new Tokens("refresh", Set.of("refresh"), "access", "id", Set.of("token"), 123456L);

        tokens.updateCurrent(token);
        Assertions.assertTrue(tokensFile.isFile());

        tokens.getAll().clear();

        tokens.init();

        Tokens current = tokens.getCurrent();
        Assertions.assertEquals("refresh", current.getRefreshToken());
        Assertions.assertEquals("access", current.getAccessToken());
        Assertions.assertEquals("id", current.getIdToken());
        Assertions.assertEquals(Set.of("refresh"), current.getRefreshScope());
        Assertions.assertEquals(Set.of("token"), current.getTokenScope());
        Assertions.assertEquals(123456L, current.getExpiresAt());

        tokens.clearCurrent();

        Assertions.assertNull(tokens.getCurrent());

        tokens.clearAll();
        Assertions.assertFalse(tokensFile.isFile());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.issuer", "dummy",
                    "kct.tokens.file", "${java.io.tmpdir}/test-kct-tokens.yaml"
            );
        }

    }

}
