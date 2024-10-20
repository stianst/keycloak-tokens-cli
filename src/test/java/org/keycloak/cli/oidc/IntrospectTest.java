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
public class IntrospectTest {

    @Test
    public void testIntrospect(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("introspect", "-c=test-service-account");
        LauncherAssertions.assertSuccess(result);

        OpenIDAssertions.assertTokenIntrospectionResponse(result.getOutput());
    }

}
