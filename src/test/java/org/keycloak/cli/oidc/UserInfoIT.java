package org.keycloak.cli.oidc;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.PasswordProfile;

@QuarkusMainIntegrationTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class UserInfoIT {

    @Test
    @Launch({"userinfo"})
    public void userinfo(LaunchResult result) throws JsonProcessingException {
        OpenIDAssertions.assertUserInfoResponse(result.getOutput());
    }

}
