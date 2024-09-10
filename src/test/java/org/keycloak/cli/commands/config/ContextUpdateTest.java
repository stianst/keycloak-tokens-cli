package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
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
import org.keycloak.cli.enums.Flow;

import java.io.IOException;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith(ConfigTestProfile.class)
public class ContextUpdateTest {

    @Test
    public void testUpdateContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "context", "update", "-c=test-service-account",
                "--client-secret",
                "--flow=device");

        LauncherAssertions.assertSuccess(result, "Context 'test-service-account' updated");

        Config.Context context = ConfigTestProfile.getInstance().loadConfig().issuers().get("test-issuer").contexts().get("test-service-account");
        Assertions.assertNull(context.client().secret());
        Assertions.assertEquals(Flow.DEVICE, context.flow());
    }

}
