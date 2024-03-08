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
@TestProfile(ClientCreateIT.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ClientCreateIT {

    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("client", "create", "-i=issuer2",
                "-c=myclient",
                "--id=my-client-id",
                "--secret=client-secret",
                "--flow=device");

        Assertions.assertEquals("client=myclient created in issuer=issuer2", result.getOutput());

        result = launcher.launch("client", "view", "-i=issuer2", "-c=myclient");
        Assertions.assertEquals("id=my-client-id\n" +
                "secret=*************\n" +
                "flow=device", result.getOutput());
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
