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
public class PasswordFlowTest {

    @Test
    public void testPasswordFlow(QuarkusMainLauncher launcher) {
        LaunchResult tokenResult = launcher.launch("token", "-c=test-password");
        LauncherAssertions.assertSuccess(tokenResult);

        String accessToken = tokenResult.getOutput();

        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(accessToken);
        Assertions.assertEquals("test-password", jsonNode.get("azp").asText());

        LaunchResult revokeResult = launcher.launch("revoke", "-c=test-password", "--token", accessToken);
        Assertions.assertEquals("Token revoked", revokeResult.getOutput());
    }

}
