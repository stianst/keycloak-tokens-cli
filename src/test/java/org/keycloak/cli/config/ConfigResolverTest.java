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
    private Config.Context context2;

    @BeforeEach
    public void createValidConfig() {
        config = new Config();

        issuer = new Config.Issuer();
        issuer.setUrl("https://myissuer");

        config.setDefaultContext("mycontext");

        config.setIssuers(Map.of("myissuer", issuer));

        Config.Client client = new Config.Client();
        client.setId("myissuer-client-id");
        client.setSecret("myissuer-client-secret");
        client.setFlow(Flow.DEVICE);
        config.getIssuers().get("myissuer").setClients(Map.of("myissuer-client", client));

        context = new Config.Context();
        context.setIssuerRef("myissuer");
        context.setClient("myclient");
        context.setFlow(Flow.PASSWORD);
        context.setUser("myuser");
        context.setUserPassword("myuserpassword");

        context2 = new Config.Context();
        context2.setIssuerRef("myissuer");
        context2.setClientRef("myissuer-client");
        context2.setUser("myuser");
        context2.setUserPassword("myuserpassword");

        config.setContexts(Map.of(
                "mycontext", context,
                "mycontext2", context2
        ));
    }

    @Test
    public void resolveIssuer() {
        Assertions.assertNull(context.getIssuer());
        Assertions.assertEquals("myissuer", context.getIssuerRef());

        ConfigRefResolver.resolve(config);

        Assertions.assertEquals("https://myissuer", context.getIssuer());
    }

    @Test
    public void resolveClient() {
        Assertions.assertEquals("myissuer-client", context2.getClientRef());

        ConfigRefResolver.resolve(config);

        Assertions.assertEquals("myissuer-client-id", context2.getClient());
        Assertions.assertEquals("myissuer-client-secret", context2.getClientSecret());
        Assertions.assertEquals(Flow.DEVICE, context2.getFlow());
    }

}
