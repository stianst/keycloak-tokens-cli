package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.ConfigFromFileProfile;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.mock.MockConfigFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(ConfigFromFileProfile.class)
public class ConfigViewIT {

    MockConfigFile mockConfigFile = new MockConfigFile();

    @Test
    public void view(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "view");
        assertOutput("ConfigViewIT.view.output", result);
    }

    @Test
    public void viewSpecificContext(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "view", "-c=mycontext2");
        assertOutput("ConfigViewIT.viewSpecificContext.output", result);
    }

    private void assertOutput(String resource, LaunchResult result) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resource)) {
            String expectedOutput = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Assertions.assertEquals(expectedOutput, result.getOutput());
        }
    }

}
