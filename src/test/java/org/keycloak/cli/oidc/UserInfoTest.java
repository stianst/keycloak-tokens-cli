package org.keycloak.cli.oidc;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith({ConfigTestProfile.class})
public class UserInfoTest {

    @Test
    public void userinfo(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("userinfo", "-c=test-password");
        LauncherAssertions.assertSuccess(result);

        OpenIDAssertions.assertUserInfoResponse(result.getOutput());
    }

}