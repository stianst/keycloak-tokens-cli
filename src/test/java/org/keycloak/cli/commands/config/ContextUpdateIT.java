package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.enums.Flow;

import java.io.IOException;
import java.util.Set;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ContextUpdateIT {

    @Test
    public void testUpdateContext(QuarkusMainLauncher launcher) throws IOException {
        Config config = ConfigTestProfile.loadConfig();
        config.getIssuers().put("myissuer", new Config.Issuer("http://localhost:8080", null));
        config.getContexts().put("mycontext", new Config.Context(new Config.Issuer("http://localhost:8080", null), Flow.PASSWORD, new Config.Client("myclient", "mysecret"), new Config.User("username", "password"), Set.of("one")));
        ConfigTestProfile.updateConfig(config);

        LaunchResult result = launcher.launch("config", "context", "update", "-c=mycontext",
                "--iss",
                "--iss-ref=myissuer",
                "--client-secret",
                "--flow=device",
                "--user",
                "--user-password",
                "--scope");

        LauncherAssertions.assertSuccess(result, "Context 'mycontext' updated");

        Config.Context context = ConfigTestProfile.loadConfig().getContexts().get("mycontext");
        Assertions.assertNull(context.getIssuer().getUrl());
        Assertions.assertEquals("myissuer", context.getIssuer().getRef());
        Assertions.assertNull(context.getClient().getSecret());
        Assertions.assertNull(context.getUser());
        Assertions.assertNull(context.getScope());
    }

}
