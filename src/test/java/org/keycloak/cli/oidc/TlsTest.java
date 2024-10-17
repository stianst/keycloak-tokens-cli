package org.keycloak.cli.oidc;


import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith({ConfigTestProfile.class})
public class TlsTest {

    private static boolean CONTEXT_CREATED = false;

    @Test
    public void testTlsWithJavaKeystore(QuarkusMainLauncher launcher) {
        testTlsFlow(launcher, "truststore.jks");
    }

    @Test
    public void testTlsWithPkcs12(QuarkusMainLauncher launcher) {
        testTlsFlow(launcher, "truststore.pfx");
    }

    private void testTlsFlow(QuarkusMainLauncher launcher, String truststore) {
        LauncherAssertions.assertSuccess(launcher.launch("config", "update", "--truststore-path=test-server/cert/" + truststore, "--truststore-password=mypassword"));
        if (!CONTEXT_CREATED) {
            LauncherAssertions.assertSuccess(launcher.launch("config", "context", "create", "-c=tls-test", "--iss=https://localhost:8443/realms/test", "--flow=client", "--client=test-service-account", "--client-secret=ErHRtK0BXg92kWMVfpJndwJsqn7b9BX5"));
            CONTEXT_CREATED = true;
        }

        LaunchResult tokenResult = launcher.launch("token", "-c=tls-test");

        String accessToken = tokenResult.getOutput();
        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(accessToken);
        Assertions.assertEquals("https://localhost:8443/realms/test", jsonNode.get("iss").asText());
    }

}
