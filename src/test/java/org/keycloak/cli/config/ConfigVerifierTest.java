package org.keycloak.cli.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.keycloak.cli.enums.Flow;

import java.util.Collections;
import java.util.Map;

public class ConfigVerifierTest {

    private Config config;
    private VariableResolver variableResolver;

    @BeforeEach
    public void createValidConfig() {
        config = new Config(
                "mycontext",
                true,
                Map.of("myissuer", new Config.Issuer("https://issuer", null)),
                Map.of("mycontext", new Config.Context(new Config.Issuer("https://issuer", null), Flow.PASSWORD, new Config.Client("myclient", null), new Config.User("myuser", "myuserpassword"), null)),
                null);
        variableResolver = new VariableResolver();
        variableResolver.init(Collections.emptyMap());
    }

    @Test
    public void testValidConfig() throws Throwable {
        verify().execute();
    }

    @Test
    public void testInvalidDefaultContext() {
        config.setDefaultContext("nosuch");
        assertError("Default context 'nosuch' not found");
    }

    @Test
    public void testInvalidIssuer() {
        Config.Issuer issuer = config.getIssuers().get("myissuer");

        issuer.setUrl(null);
        assertIssuerError("missing issuer url");
        issuer.setUrl("localhost:8080");
        assertIssuerError("invalid issuer url");
    }

    @Test
    public void testInvalidContextIssuer() {
        Config.Context context = config.getContexts().get("mycontext");
        Config.Issuer issuer = context.getIssuer();

        context.setIssuer(null);
        assertContextError("mycontext", "issuer not configured");
        context.setIssuer(issuer);

        issuer.setUrl("localhost:8080");
        assertContextError("mycontext", "invalid issuer url");

        issuer.setUrl("http://localhost:8080");
        issuer.setRef("localhost");

        assertContextError("mycontext", "both issuer url and issuer ref set");

        issuer.setUrl(null);
        assertContextError("mycontext", "issuer ref 'localhost' not found");
    }

    @Test
    public void testUserMissingForPassword() {
        Config.Context context = config.getContexts().get("mycontext");
        Config.User user = context.getUser();

        context.setFlow(Flow.PASSWORD);
        context.setUser(null);
        assertContextError("mycontext", "user required for flow 'password'");
        context.setUser(user);

        user.setUsername(null);
        assertContextError("mycontext", "user username required for flow 'password'");
        user.setUsername("user");

        user.setPassword(null);
        assertContextError("mycontext", "user password required for flow 'password'");
    }

    @Test
    public void testDeviceFlow() {
        Config.Context context = config.getContexts().get("mycontext");
        context.setFlow(Flow.DEVICE);

        assertContextError("mycontext", "user set for flow 'device'");
    }

    @Test
    public void testClientFlow() {
        Config.Context context = config.getContexts().get("mycontext");
        Config.User user = context.getUser();
        context.setFlow(Flow.CLIENT);
        context.setUser(null);

        context.getClient().setSecret(null);
        assertContextError("mycontext", "client secret required for flow 'client'");
        context.getClient().setSecret("client-secret");

        context.setUser(user);
        assertContextError("mycontext", "user set for flow 'client'");
        context.setUser(null);
    }

    @Test
    public void testBrowser() {
        Config.Context context = config.getContexts().get("mycontext");
        context.setFlow(Flow.BROWSER);

        assertContextError("mycontext", "user set for flow 'browser'");
    }

    @Test
    public void testFlowMissing() {
        Config.Context context = config.getContexts().get("mycontext");
        context.setFlow(null);

        assertContextError("mycontext", "missing flow");
    }

    private void assertIssuerError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("Issuer 'myissuer' invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertContextError(String expectedContextId, String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("Context '" + expectedContextId + "' invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals(expectedMessage, configException.getMessage());
    }

    private Executable verify() {
        return () -> ConfigVerifier.verify(config, variableResolver);
    }

}
