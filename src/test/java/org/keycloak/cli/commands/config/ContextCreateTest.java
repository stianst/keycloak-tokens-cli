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
import org.keycloak.cli.container.KeycloakTestResource;

import java.io.IOException;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith(ConfigTestProfile.class)
public class ContextCreateTest {

    @Test
    public void testCreateContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "context", "create", "-c=mycontext3",
                "--iss=test-issuer",
                "--flow=device",
                "--client=myclient");
        LauncherAssertions.assertSuccess(result, "Context 'mycontext3' created");
        Assertions.assertNotNull(ConfigTestProfile.getInstance().loadConfig().issuers().get("test-issuer").contexts().get("mycontext3"));
    }

}
