package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.KubeCtlAssertions;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.kubectl.ExecCredentialRepresentation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith({ConfigTestProfile.class})
public class TokenTest {

    @Test
    public void token(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("token");
        LauncherAssertions.assertSuccess(result);
        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(result.getOutput());
        Assertions.assertNull(jsonNode.get("email"));
    }

    @Test
    public void tokenDecode(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("token", "--decode");
        LauncherAssertions.assertSuccess(result);
        OpenIDAssertions.assertDecodedToken(result.getOutput());
    }

    @Test
    public void tokenKubectl(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("token", "--kubectl");
        LauncherAssertions.assertSuccess(result);
        ExecCredentialRepresentation execCredential = KubeCtlAssertions.assertExecCredential(result.getOutput());
        Assertions.assertEquals("ExecCredential", execCredential.getKind());
        OpenIDAssertions.assertEncodedToken(execCredential.getStatus().getToken());

        String expectedOutput = new String(getClass().getResourceAsStream("TokenIT.tokenKubectl").readAllBytes(), StandardCharsets.UTF_8);
        expectedOutput = expectedOutput.replace("$$TOKEN$$", execCredential.getStatus().getToken());

        Assertions.assertEquals(expectedOutput, result.getOutput());
    }

}
