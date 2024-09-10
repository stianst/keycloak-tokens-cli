package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith(ConfigTestProfile.class)
public class IssuerViewTest {

    @Test
    public void testViewSpecified(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("config", "issuer", "view", "--iss=test-issuer");
        LauncherAssertions.assertYamlOutput(result, "test-issuer", ConfigTestProfile.getInstance().getDefaultConfig().issuers().get("test-issuer"));
    }

}
