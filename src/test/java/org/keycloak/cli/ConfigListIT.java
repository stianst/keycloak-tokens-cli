package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.ConfigFromBothProfile;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.mock.MockConfigFile;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(ConfigFromBothProfile.class)
public class ConfigListIT {

    MockConfigFile mockConfigFile = new MockConfigFile();

    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "list");
        Assertions.assertEquals("<properties>, mycontext2, mycontext", result.getOutput());
    }

}
