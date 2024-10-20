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
public class IssuerDeleteTest {

    @Test
    public void test(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "issuer", "delete", "--iss=test-issuer");
        LauncherAssertions.assertSuccess(result, "Issuer 'test-issuer' deleted");
        Assertions.assertTrue(ConfigTestProfile.getInstance().loadConfig().getIssuers().isEmpty());
    }

}
