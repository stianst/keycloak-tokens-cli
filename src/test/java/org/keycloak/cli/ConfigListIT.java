package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.KeycloakTestResource;

import java.util.Map;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(ConfigListIT.Profile.class)
public class ConfigListIT {

    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "list");
        Assertions.assertEquals("<properties>, mycontext2, mycontext", result.getOutput());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", "${java.io.tmpdir}/test-kct.yaml",
                    "kct.issuer", "http://issuer"
            );
        }

    }

}
