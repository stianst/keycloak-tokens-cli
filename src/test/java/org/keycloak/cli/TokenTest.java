package org.keycloak.cli;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class TokenTest {

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
    @Launch({"token", "--scope=openid,email"})
    public void tokenCustomScope(LaunchResult result) {
        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(result.getOutput());
        Assertions.assertNotNull(jsonNode.get("email"));
    }

}
