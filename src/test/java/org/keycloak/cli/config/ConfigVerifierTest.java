package org.keycloak.cli.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.keycloak.cli.enums.Flow;

import java.util.Map;

public class ConfigVerifierTest {

    private Config config;
    private Config.Context context;
    private Config.Issuer issuer;

    @BeforeEach
    public void createValidConfig() {
        config = new Config();
        config.setDefaultContext("mycontext");

        issuer = new Config.Issuer();
        issuer.setUrl("https://myissuer");

        config.setDefaultContext("mycontext");

        config.setIssuers(Map.of("myissuer", issuer));

        context = new Config.Context();
        context.setIssuer("https://issuer");
        context.setClient("myclient");
        context.setFlow(Flow.PASSWORD);
        context.setUser("myuser");
        context.setUserPassword("myuserpassword");

        config.setContexts(Map.of("mycontext", context));
    }

    @Test
    public void validConfig() throws Throwable {
        verify().execute();
    }

    @Test
    public void invalidDefaultContext() {
        config.setDefaultContext(null);
        assertError("default context not set");
        config.setDefaultContext("nosuch");
        assertError("default context=nosuch not found");
    }

    @Test
    public void invalidIssuer() {
        issuer.setUrl(null);
        assertIssuerError("missing url");
        issuer.setUrl("localhost:8080");
        assertIssuerError("invalid url");
    }

    @Test
    public void invalidContextIssuer() {
        context.setIssuer(null);
        assertContextError("missing issuer");
        context.setIssuer("localhost:8080");
        assertContextError("invalid issuer");

        context.setIssuer("http://localhost:8080");
        context.setIssuerRef("localhost");

        assertContextError("both issuer and issuer-ref set");

        context.setIssuer(null);
        assertContextError("issuer-ref=localhost not found");
    }

    @Test
    public void userMissingForPassword() {
        context.setFlow(Flow.PASSWORD);
        context.setUser(null);
        assertContextError("user required for flow=password");
        context.setUser("asdf");
        context.setUserPassword(null);
        assertContextError("user-password required for flow=password");
    }

    @Test
    public void userSetForDevice() {
        context.setFlow(Flow.DEVICE);
        context.setUser("asdf");
        assertContextError("user set for flow=device");
        context.setUser(null);
        context.setUserPassword("pass");
        assertContextError("user-password set for flow=device");
    }

    private void assertIssuerError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("issuer=myissuer invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertContextError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("context=mycontext invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals(expectedMessage, configException.getMessage());
    }

    private Executable verify() {
        return () -> ConfigVerifier.verify(config);
    }

}
