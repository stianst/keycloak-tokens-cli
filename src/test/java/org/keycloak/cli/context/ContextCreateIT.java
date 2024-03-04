package org.keycloak.cli.context;

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

import java.util.Map;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(ContextCreateIT.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ContextCreateIT {

    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("context", "create", "-c=mycontext3",
                "--issuer=http://localhost",
                "--flow=device",
                "--client=myclient");

        Assertions.assertEquals("context=mycontext3 created", result.getOutput());

        result = launcher.launch("context", "list");
        Assertions.assertEquals("mycontext  mycontext2  mycontext3", result.getOutput());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath()
            );
        }
    }

}
