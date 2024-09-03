package org.keycloak.cli.tokens;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.oidc.Tokens;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith({ConfigTestProfile.class})
public class ClearIT {

    @BeforeEach
    public void addSomeTokens() throws IOException {
        TokenStore tokenStore = new TokenStore();
        for (String i : List.of("test-service-account", "test-password")) {
            tokenStore.getTokens().put(i, new Tokens("refresh-" + i, Set.of("ref-scope-" + i), "access-" + i, "id-" + i, Set.of("tok-scope-" + i), 1L));
        }
        ConfigTestProfile.updateTokens(tokenStore);
    }

    @Test
    public void clearCurrent(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("clear");
        LauncherAssertions.assertSuccess(result, "Cleared tokens for context 'test-service-account'");
        assertStoredContexts(Set.of("test-password"));
    }

    @Test
    public void clearSpecific(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("clear", "-c=test-password");
        LauncherAssertions.assertSuccess(result, "Cleared tokens for context 'test-password'");
        assertStoredContexts(Set.of("test-service-account"));
    }

    @Test
    public void clearAll(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("clear", "--all");
        LauncherAssertions.assertSuccess(result, "Cleared all stored tokens");
        Assertions.assertEquals(0, ConfigTestProfile.TOKENS_FILE.length());
    }

    private void assertStoredContexts(Set<String> expectedStoredContexts) throws IOException {
        Assertions.assertEquals(expectedStoredContexts, ConfigTestProfile.loadTokens().getTokens().keySet());
    }

}
