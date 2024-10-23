package org.keycloak.cli.commands.config;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigTestProfile.class)
public class IssuerDeleteTest {

    @Test
    public void test(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "issuer", "delete", "--iss=test-issuer");
        LauncherAssertions.assertFailure(result, "Issuer 'test-issuer' contains contexts, please delete contexts first or use --force");

        result = launcher.launch("config", "issuer", "delete", "--iss=test-issuer", "--force");
        LauncherAssertions.assertSuccess(result, "Issuer 'test-issuer' deleted");

        Config config = ConfigTestProfile.getInstance().loadConfig();
        Assertions.assertTrue(config.getIssuers().isEmpty());
        Assertions.assertNull(config.getDefaultContext());
    }

}
