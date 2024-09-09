package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.container.KeycloakTestResource;

import java.io.IOException;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ConfigUpdateTest {

    @Test
    public void testUpdate(QuarkusMainLauncher launcher) throws IOException {
        Config config = ConfigTestProfile.loadConfig();
        Assertions.assertTrue(config.storeTokens());
        Assertions.assertNotEquals("test-password", config.defaultContext());

        LaunchResult result = launcher.launch("config", "update", "--store-tokens=false", "--default-context=test-password");
        LauncherAssertions.assertSuccess(result, "Config updated");

        config = ConfigTestProfile.loadConfig();
        Assertions.assertFalse(config.storeTokens());
        Assertions.assertEquals("test-password", config.defaultContext());
    }

}
