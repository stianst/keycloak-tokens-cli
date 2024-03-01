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

@QuarkusTest
@TestProfile(ConfigFromFileContextFromPropTest.Profile.class)
@ExtendWith(MockConfigFile.class)
public class ConfigFromFileContextFromPropTest {

    @Inject
    ConfigService config;

    @Test
    public void getConfig() {
        Assertions.assertEquals("mycontext2", config.getContext());
        Assertions.assertFalse(config.isConfiguredFromProperties());
        Assertions.assertEquals("http://myissuer2", config.getIssuer());
        Assertions.assertEquals(Flow.DEVICE, config.getFlow());
        Assertions.assertEquals("myclient2", config.getClient());
        Assertions.assertNull(config.getClientSecret());
        Assertions.assertNull(config.getUserPassword());
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath(),
                    "kct.context", "mycontext2"
            );
        }
    }

}
