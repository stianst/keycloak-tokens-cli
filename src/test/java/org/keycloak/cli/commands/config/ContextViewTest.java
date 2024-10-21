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
public class ContextViewTest {

    @Test
    public void testViewDefaultContext(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "context", "view");
        LauncherAssertions.assertYamlOutput(result, "test-service-account", ConfigTestProfile.getInstance().getDefaultConfig().getIssuers().get("test-issuer").getContexts().get("test-service-account"));
    }

    @Test
    public void testViewSpecifiedContext(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "context", "view", "-c=test-password");
        LauncherAssertions.assertYamlOutput(result, "test-password", ConfigTestProfile.getInstance().getDefaultConfig().getIssuers().get("test-issuer").getContexts().get("test-password"));
    }

}
