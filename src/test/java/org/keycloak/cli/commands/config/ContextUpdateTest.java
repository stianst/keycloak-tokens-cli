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
import org.keycloak.cli.enums.Flow;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigTestProfile.class)
public class ContextUpdateTest {

    @Test
    public void testUpdateContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "context", "update", "-c=test-service-account",
                "--client-secret",
                "--flow=device");

        LauncherAssertions.assertSuccess(result, "Context 'test-service-account' updated");

        Config.Context context = ConfigTestProfile.getInstance().loadConfig().getIssuers().get("test-issuer").getContexts().get("test-service-account");
        Assertions.assertNull(context.getClient().getSecret());
        Assertions.assertEquals(Flow.DEVICE, context.getFlow());
    }

    @Test
    public void testUpdateDefaultContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "context", "update", "-c=test-password",
                "--default");
        LauncherAssertions.assertSuccess(result, "Context 'test-password' updated");
        Assertions.assertEquals("test-password", ConfigTestProfile.getInstance().loadConfig().getDefaultContext());
    }

}
