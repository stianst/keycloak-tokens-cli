package org.keycloak.cli.oidc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

import java.util.Map;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ClientSecretIT.Profile.class)
public class ClientSecretIT {


    @Test
    public void testToken(QuarkusMainLauncher launcher) throws JsonProcessingException {
        LaunchResult tokenResult = launcher.launch("token");

        String accessToken = tokenResult.getOutput();

        JsonNode jsonNode = OpenIDAssertions.assertEncodedToken(accessToken);
        Assertions.assertEquals("test-client-secret", jsonNode.get("azp").asText());

        LaunchResult userinfoResult = launcher.launch("userinfo", "--access-token", accessToken);

        UserInfo userInfo = OpenIDAssertions.assertUserInfoResponse(userinfoResult.getOutput());
        Assertions.assertEquals("test-user", userInfo.getPreferredUsername());

        LaunchResult revokeResult = launcher.launch("revoke", "--type=access", "--token", accessToken);
        Assertions.assertEquals("Token revoked", revokeResult.getOutput());
    }


    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.issuer", "${keycloak.issuer}",
                    "kct.flow", "password",
                    "kct.client", "test-client-secret",
                    "kct.client-secret", "BahqwSovZrzxCgy7asnnC2tGX1GdeE2u",
                    "kct.user", "test-user",
                    "kct.user-password", "test-user-password",
                    "kct.scope", "openid"
            );
        }

    }

}
