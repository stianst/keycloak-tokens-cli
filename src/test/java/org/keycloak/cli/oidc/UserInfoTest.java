package org.keycloak.cli.oidc;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.assertion.OpenIDAssertions;

@QuarkusMainTest
@ExtendWith({ConfigTestProfile.class})
public class UserInfoTest {

    @Test
    public void userinfo(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("userinfo", "-c=test-password");
        LauncherAssertions.assertSuccess(result);

        OpenIDAssertions.assertUserInfoResponse(result.getOutput());
    }

    @Test
    public void userinfoMissingOpenidScope(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("userinfo");
        LauncherAssertions.assertFailure(result, "Request failed: status=403, error=insufficient_scope, description='Missing openid scope', url=http://localhost:8080/realms/test/protocol/openid-connect/userinfo");
    }

}
