package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ContextViewIT {

    @Test
    public void testViewDefaultContext(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "context", "view");
        LauncherAssertions.assertYamlOutput(result, "test-service-account", ConfigTestProfile.DEFAULT_CONFIG.getContexts().get("test-service-account"));
    }

    @Test
    public void testViewSpecifiedContext(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "context", "view", "-c=test-password");
        LauncherAssertions.assertYamlOutput(result, "test-password", ConfigTestProfile.DEFAULT_CONFIG.getContexts().get("test-password"));
    }

    @Test
    public void testViewAll(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "context", "view", "--all");
        LauncherAssertions.assertYamlOutput(result, null, ConfigTestProfile.DEFAULT_CONFIG.getContexts());
    }

}
