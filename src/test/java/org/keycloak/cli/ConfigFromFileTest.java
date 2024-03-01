package org.keycloak.cli;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.ConfigFromFileProfile;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.mock.MockConfigFile;

import java.util.Set;

@QuarkusTest
@TestProfile(ConfigFromFileProfile.class)
public class ConfigFromFileTest {
    @Inject
    MockConfigFile mockConfigFile;

    @Inject
    ConfigService config;


    @Test
    public void getConfig() {
        Assertions.assertFalse(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://myissuer", config.getIssuer());
        Assertions.assertEquals(Flow.DEVICE, config.getFlow());
        Assertions.assertEquals("myclient", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertEquals("myuser", config.getUser());
        Assertions.assertEquals("myuserPassword", config.getUserPassword());
        Assertions.assertEquals(Set.of("openid", "email"), config.getScope());
    }

}
