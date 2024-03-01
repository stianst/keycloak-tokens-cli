package org.keycloak.cli;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.Flow;

import java.util.Map;

@QuarkusTest
@TestProfile(ConfigFromPropertiesTest.Profile.class)
public class ConfigFromPropertiesTest {

    @Inject
    ConfigService config;

    @Test
    public void getConfig() {
        Assertions.assertNull(config.getContext());
        Assertions.assertTrue(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://localhost:8080/something", config.getIssuer());
        Assertions.assertEquals(Flow.PASSWORD, config.getFlow());
        Assertions.assertEquals("test-password", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertEquals("test-user", config.getUser());
        Assertions.assertEquals("test-user-password", config.getUserPassword());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.issuer", "http://localhost:8080/something",
                    "kct.flow", "password",
                    "kct.client", "test-password",
                    "kct.user", "test-user",
                    "kct.user-password", "test-user-password",
                    "kct.scopes", "openid"
            );
        }

    }

}
