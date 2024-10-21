package org.keycloak.cli.commands.config;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;

@QuarkusMainTest
@ExtendWith(ConfigTestProfile.class)
public class ConfigViewTest {

    @Test
    public void testView(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "view");
        LauncherAssertions.assertYamlOutput(result, null, ConfigTestProfile.getInstance().getDefaultConfig());
    }

}
