package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.KubeCtlAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.kubectl.ExecCredentialRepresentation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class TokenIT {

    @Test
    @Launch("token")
    public void token(LaunchResult result) {
        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(result.getOutput());
        Assertions.assertNull(jsonNode.get("email"));
    }

    @Test
    @Launch({"token", "--decode"})
    public void tokenDecode(LaunchResult result) {
        OpenIDAssertions.assertDecodedToken(result.getOutput());
    }

    @Test
    @Launch({"token", "--kubectl"})
    public void tokenKubectl(LaunchResult result) throws IOException {
        ExecCredentialRepresentation execCredential = KubeCtlAssertions.assertExecCredential(result.getOutput());
        Assertions.assertEquals("ExecCredential", execCredential.getKind());
        OpenIDAssertions.assertEncodedToken(execCredential.getStatus().getToken());

        String expectedOutput = new String(getClass().getResourceAsStream("TokenIT.tokenKubectl").readAllBytes(), StandardCharsets.UTF_8);
        expectedOutput = expectedOutput.replace("$$TOKEN$$", execCredential.getStatus().getToken());

        Assertions.assertEquals(expectedOutput, result.getOutput());
    }

    @Test
    @Launch({"token", "--scope=openid,email"})
    public void tokenCustomScope(LaunchResult result) {
        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(result.getOutput());
        Assertions.assertNotNull(jsonNode.get("email"));
    }

}
