package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith(ConfigTestProfile.class)
public class ConfigViewTest {

    @Test
    public void testView(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "view");
        LauncherAssertions.assertYamlOutput(result, null, ConfigTestProfile.getInstance().getDefaultConfig());
    }

}
