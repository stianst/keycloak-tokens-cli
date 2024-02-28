package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.PasswordProfile;

@QuarkusMainTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class TestIT {


    @Test
    public void test(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config");
    }
}
