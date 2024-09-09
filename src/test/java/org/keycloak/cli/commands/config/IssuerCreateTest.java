package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

import java.io.IOException;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class IssuerCreateTest {

    @Test
    public void testCreateIssuer(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("config", "issuer", "create", "-i=issuer3",
                "--url=http://localhost3");
        LauncherAssertions.assertSuccess(result, "Issuer 'issuer3' created");
        Assertions.assertNotNull(ConfigTestProfile.loadConfig().issuers().get("issuer3"));
    }

}
