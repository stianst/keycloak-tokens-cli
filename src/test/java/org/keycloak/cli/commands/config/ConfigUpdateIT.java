package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.container.KeycloakTestResource;

import java.io.IOException;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ConfigUpdateIT {

    @Test
    public void testUpdate(QuarkusMainLauncher launcher) throws IOException {
        Config config = ConfigTestProfile.loadConfig();
        Assertions.assertFalse(config.getStoreTokens());
        Assertions.assertNotEquals("test-password", config.getDefaultContext());

        LaunchResult result = launcher.launch("config", "update", "--store-tokens", "--default-context=test-password");
        LauncherAssertions.assertSuccess(result, "Config updated");

        config = ConfigTestProfile.loadConfig();
        Assertions.assertTrue(config.getStoreTokens());
        Assertions.assertEquals("test-password", config.getDefaultContext());
    }

}
