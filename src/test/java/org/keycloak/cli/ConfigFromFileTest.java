package org.keycloak.cli;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.MockConfigFile;
import org.keycloak.cli.enums.Flow;

import java.util.Map;
import java.util.Set;

@QuarkusTest
@TestProfile(ConfigFromFileTest.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ConfigFromFileTest {

    @Inject
    ConfigService config;


    @Test
    public void getConfig() {
        Assertions.assertFalse(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://issuer", config.getIssuer());
        Assertions.assertEquals(Flow.PASSWORD, config.getFlow());
        Assertions.assertEquals("test-password", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertEquals("test-user", config.getUser());
        Assertions.assertEquals("test-user-password", config.getUserPassword());
        Assertions.assertEquals(Set.of("openid", "email"), config.getScope());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath()
            );
        }
    }


}
