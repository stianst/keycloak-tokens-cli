package org.keycloak.cli.commands.config;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigTestProfile.class)
public class ConfigUpdateTest {

    @Test
    public void testUpdate(QuarkusMainLauncher launcher) throws IOException {
        Config config = ConfigTestProfile.getInstance().loadConfig();
        Assertions.assertTrue(config.getStoreTokens());
        Assertions.assertNotEquals("test-password", config.getDefaultContext());

        LaunchResult result = launcher.launch("config", "update", "--store-tokens=false", "--default-context=test-password");
        LauncherAssertions.assertSuccess(result, "Config updated");

        config = ConfigTestProfile.getInstance().loadConfig();
        Assertions.assertFalse(config.getStoreTokens());
        Assertions.assertEquals("test-password", config.getDefaultContext());
    }

    @Test
    public void testUpdateTruststore(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "update", "--truststore-path=/mypath", "--truststore-password=test-password");
        LauncherAssertions.assertSuccess(result, "Config updated");

        Config config = ConfigTestProfile.getInstance().loadConfig();
        Assertions.assertEquals("/mypath", config.getTruststore().getPath());
        Assertions.assertEquals("test-password", config.getTruststore().getPassword());
    }

}
