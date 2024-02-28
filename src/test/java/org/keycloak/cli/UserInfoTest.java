package org.keycloak.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainIntegrationTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class UserInfoTest {

    @Test
    @Launch({"userinfo"})
    public void userinfo(LaunchResult result) throws JsonProcessingException {
        OpenIDAssertions.assertUserInfoResponse(result.getOutput());
    }

}
