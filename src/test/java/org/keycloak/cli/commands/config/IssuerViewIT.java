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
public class IssuerViewIT {

    @Test
    public void testViewSpecified(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "issuer", "view", "--iss=test-issuer");
        LauncherAssertions.assertYamlOutput(result, "test-issuer", ConfigTestProfile.DEFAULT_CONFIG.getIssuers().get("test-issuer"));
    }

    @Test
    public void testViewAll(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "issuer", "view", "--all");
        LauncherAssertions.assertYamlOutput(result, null, ConfigTestProfile.DEFAULT_CONFIG.getIssuers());
    }

}
