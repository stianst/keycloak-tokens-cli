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

import java.io.IOException;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ConfigViewIT {

    @Test
    public void testView(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "view");
        LauncherAssertions.assertYamlOutput(result, null, ConfigTestProfile.DEFAULT_CONFIG);
    }

}
