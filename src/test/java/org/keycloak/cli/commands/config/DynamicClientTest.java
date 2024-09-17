package org.keycloak.cli.commands.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.LauncherAssertions;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.testcontainers.shaded.org.hamcrest.CoreMatchers;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@QuarkusMainTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith(ConfigTestProfile.class)
public class DynamicClientTest {

    private Keycloak adminClient;

    @BeforeEach
    public void before() {
        adminClient = KeycloakBuilder.builder().serverUrl("http://localhost:8080").realm("master").grantType("client_credentials").clientId("temp-admin").clientSecret("mysecret").build();
    }

    @Test
    public void testRegisterClient(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult createContext = launcher.launch("config", "context", "create", "-c=mycontext4",
                "--iss=test-issuer",
                "--flow=browser",
                "--scope=roles,email,phone",
                "--create-client");
        LauncherAssertions.assertSuccess(createContext, "Context 'mycontext4' created");

        Config.Context context = ConfigTestProfile.getInstance().loadConfig().findContext("mycontext4");
        Assertions.assertNotNull(context.client().clientId());
        Assertions.assertNotNull(context.client().secret());
        Assertions.assertNotNull(context.client().registrationToken());
        Assertions.assertNotNull(context.client().registrationUrl());

        ClientRepresentation client = getClient(context);
        Assertions.assertNotNull(client);
        assertScope(client.getOptionalClientScopes(), "roles", "email", "phone");

        LaunchResult updateContext = launcher.launch("config", "context", "update", "-c=mycontext4",
                "--scope=email");
        LauncherAssertions.assertSuccess(updateContext, "Context 'mycontext4' updated");

        Config.Context updatedContext = ConfigTestProfile.getInstance().loadConfig().findContext("mycontext4");
        Assertions.assertNotEquals(context.client().registrationToken(), updatedContext.client().registrationToken());

        assertScope(getClient(context).getOptionalClientScopes(), "email");

        LaunchResult deleteContext = launcher.launch("config", "context", "delete", "-c=mycontext4");
        LauncherAssertions.assertSuccess(deleteContext, "Context 'mycontext4' deleted");

        Assertions.assertEquals(0, adminClient.realms().realm("test").clients().findByClientId(context.client().clientId()).size());
    }

    private ClientRepresentation getClient(Config.Context context) {
        List<ClientRepresentation> clients = adminClient.realms().realm("test").clients().findByClientId(context.client().clientId());
        if (clients.size() == 1) {
            return clients.get(0);
        } else if (clients.isEmpty()) {
            return null;
        } else {
            throw new RuntimeException("Found " + clients.size() + " clients, expected only one");
        }
    }

    private void assertScope(Collection<String> actualScope, String... expectedScope) {
        MatcherAssert.assertThat(actualScope, CoreMatchers.hasItems(expectedScope));
        MatcherAssert.assertThat(actualScope, Matchers.hasSize(expectedScope.length));
    }

}
