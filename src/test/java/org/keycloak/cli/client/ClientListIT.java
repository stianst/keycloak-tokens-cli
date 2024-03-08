package org.keycloak.cli.client;

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
@TestProfile(ClientListIT.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ClientListIT {

    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("client", "list", "-i=issuer1");
        Assertions.assertEquals("client1  client2", result.getOutput());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath(),
                    "kct.issuer", "http://issuer"
            );
        }

    }

}
