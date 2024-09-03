package org.keycloak.cli.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ConfigResolverTest {

    private Config config;

    @BeforeEach
    public void testIssuerResolve() {
        config = new Config(
                "mycontext",
                false,
                Map.of("myissuer", new Config.Issuer("https://myissuer", null)),
                Map.of("mycontext", new Config.Context(new Config.Issuer(null, "myissuer"), null, null, null, null)),
                null
        );
    }

    @Test
    public void resolveIssuer() {
        Config.Context context = config.getContexts().get("mycontext");

        Assertions.assertEquals("myissuer", context.getIssuer().getRef());
        Assertions.assertNull(context.getIssuer().getUrl());

        ConfigRefResolver.resolve(config);

        Assertions.assertEquals("https://myissuer", context.getIssuer().getUrl());
        Assertions.assertNull(context.getIssuer().getRef());
    }

}
