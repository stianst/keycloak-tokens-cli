package org.keycloak.cli;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.ConfigFromFileContextFromPropProfile;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.mock.MockConfigFile;

@QuarkusTest
@TestProfile(ConfigFromFileContextFromPropProfile.class)
public class ConfigFromFileContextFromPropTest {
    @Inject
    MockConfigFile mockConfigFile;

    @Inject
    ConfigService config;

    @Test
    public void getConfig() {
        Assertions.assertEquals("mycontext2", config.getContext());
        Assertions.assertFalse(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://myissuer2", config.getIssuer());
        Assertions.assertEquals(Flow.PASSWORD, config.getFlow());
        Assertions.assertEquals("myclient2", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertEquals("myuser2", config.getUser());
        Assertions.assertNull(config.getUserPassword());
    }

}
