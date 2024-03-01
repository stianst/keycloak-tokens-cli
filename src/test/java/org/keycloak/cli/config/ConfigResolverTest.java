package org.keycloak.cli.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.enums.Flow;

import java.util.Map;

public class ConfigResolverTest {

    private Config config;
    private Config.Issuer issuer;
    private Config.Context context;

    @BeforeEach
    public void createValidConfig() {
        config = new Config();

        issuer = new Config.Issuer();
        issuer.setUrl("https://myissuer");

        config.setDefaultContext("mycontext");

        config.setIssuers(Map.of("myissuer", issuer));

        context = new Config.Context();
        context.setIssuerRef("myissuer");
        context.setClient("myclient");
        context.setFlow(Flow.PASSWORD);
        context.setUser("myuser");
        context.setUserPassword("myuserpassword");

        config.setContexts(Map.of("mycontext", context));
    }

    @Test
    public void resolveIssuer() {
        Assertions.assertNull(context.getIssuer());
        Assertions.assertEquals("myissuer", context.getIssuerRef());

        ConfigRefResolver.resolve(config);

        Assertions.assertEquals("https://myissuer", context.getIssuer());
    }

}
