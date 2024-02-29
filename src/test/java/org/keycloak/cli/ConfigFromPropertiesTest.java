package org.keycloak.cli;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.enums.Flow;

@QuarkusTest
@TestProfile(PasswordProfile.class)
public class ConfigFromPropertiesTest {

    @Inject
    ConfigService config;

    @Test
    public void getConfig() {
        Assertions.assertNull(config.getContext());
        Assertions.assertTrue(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://localhost:8080/realms/test", config.getIssuer());
        Assertions.assertEquals(Flow.PASSWORD, config.getFlow());
        Assertions.assertEquals("test-password", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertEquals("test-user", config.getUser());
        Assertions.assertEquals("test-user-password", config.getUserPassword());
    }

}
