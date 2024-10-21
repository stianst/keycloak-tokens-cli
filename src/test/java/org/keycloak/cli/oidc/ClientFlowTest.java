package org.keycloak.cli.oidc;


import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;

@QuarkusMainTest
@ExtendWith({ConfigTestProfile.class})
public class ClientFlowTest {

    @Test
    public void testClientFlow(QuarkusMainLauncher launcher) {
        LaunchResult tokenResult = launcher.launch("token", "-c=test-service-account");
        LauncherAssertions.assertSuccess(tokenResult);

        String accessToken = tokenResult.getOutput();

        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(accessToken);
        Assertions.assertEquals("test-service-account", jsonNode.get("azp").asText());

        LaunchResult revokeResult = launcher.launch("revoke", "--type=access", "--token", accessToken);
        Assertions.assertEquals("Token revoked", revokeResult.getOutput());
    }

}
