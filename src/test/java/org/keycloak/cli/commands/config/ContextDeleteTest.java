package org.keycloak.cli.commands.config;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigTestProfile.class)
public class ContextDeleteTest {

    @Test
    public void testDeleteContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "context", "delete", "--context=test-password");
        LauncherAssertions.assertSuccess(result, "Context 'test-password' deleted");
        Assertions.assertNull(ConfigTestProfile.getInstance().loadConfig().getIssuers().get("test-issuer").getContexts().get("test-password"));
    }

}
