package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.MockConfigFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(ConfigViewIT.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ConfigViewIT {

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

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", "${java.io.tmpdir}/test-kct.yaml"
            );
        }
    }

}
