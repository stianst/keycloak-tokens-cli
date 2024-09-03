package org.keycloak.cli.config;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.container.KeycloakTestResource;

@QuarkusTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(ConfigTestProfile.class)
@ExtendWith(ConfigTestProfile.class)
public class ConfigServiceTest {

    @Inject
    ConfigService configService;

    @ConfigProperty(name = "keycloak.url")
    String keycloakUrl;

    @Test
    public void testContext() {
        Assertions.assertEquals(keycloakUrl + "/realms/test", configService.getContext().getIssuer().toString());
    }

    @Test
    public void testLoadConfig() {
        Context context = configService.getContext();
        Assertions.assertEquals("test-service-account", context.getClientId().getValue());

        configService.setCurrentContext("test-password");
        context = configService.getContext();
        Assertions.assertEquals("test-password", context.getClientId().getValue());
    }

    @Test
    public void testSaveConfig() {
        Config config = configService.loadConfig();

        Config.Issuer issuer = new Config.Issuer();
        issuer.setUrl("http://localhost:8080");

        config.getIssuers().put("my-issuer", issuer);

        Config updatedConfig = configService.loadConfig();
        Assertions.assertNull(updatedConfig.getIssuers().get("my-issuer"));

        configService.saveConfig(config);

        updatedConfig = configService.loadConfig();
        Assertions.assertNotNull(updatedConfig.getIssuers().get("my-issuer"));
    }

}
