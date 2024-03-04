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
    private Config.Context clientRefContext;
    private Config.Issuer issuer;

    @BeforeEach
    public void createValidConfig() {
        config = new Config();
        config.setDefaultContext("mycontext");

        issuer = new Config.Issuer();
        issuer.setUrl("https://myissuer");

        Config.Client issuerClient = new Config.Client();
        issuerClient.setId("myissuer-client-id");
        issuerClient.setSecret("myissuer-client-secret");
        issuerClient.setFlow(Flow.DEVICE);
        issuer.setClients(Map.of("myissuer-client", issuerClient));

        config.setDefaultContext("mycontext");

        config.setIssuers(Map.of("myissuer", issuer));

        context = new Config.Context();
        context.setIssuer("https://issuer");
        context.setClient("myclient");
        context.setFlow(Flow.PASSWORD);
        context.setUser("myuser");
        context.setUserPassword("myuserpassword");

        clientRefContext = new Config.Context();
        clientRefContext.setIssuer(null);
        clientRefContext.setIssuerRef("myissuer");
        clientRefContext.setClientRef("myissuer-client");

        config.setContexts(Map.of(
                "mycontext", context,
                "mycontext2", clientRefContext
        ));
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
        assertContextError("mycontext", "missing issuer");
        context.setIssuer("localhost:8080");
        assertContextError("mycontext", "invalid issuer");

        context.setIssuer("http://localhost:8080");
        context.setIssuerRef("localhost");

        assertContextError("mycontext", "both issuer and issuer-ref set");

        context.setIssuer(null);
        assertContextError("mycontext", "issuer-ref=localhost not found");
    }

    @Test
    public void invalidIssuerClient() {
        issuer.getClients().get("myissuer-client").setId(null);
        assertError("issuer=myissuer invalid: client=myissuer-client missing id");
        issuer.getClients().get("myissuer-client").setId("myissuer-client-id");

        issuer.getClients().get("myissuer-client").setFlow(null);
        assertError("issuer=myissuer invalid: client=myissuer-client missing flow");
        issuer.getClients().get("myissuer-client").setFlow(Flow.DEVICE);
    }

    @Test
    public void invalidClientRef() {
        clientRefContext.setClient("invalid");
        assertContextError("mycontext2", "both client and client-ref set");
        clientRefContext.setClient(null);

        clientRefContext.setClientSecret("invalid");
        assertContextError("mycontext2", "both client-secret and client-ref set");
        clientRefContext.setClientSecret(null);

        clientRefContext.setFlow(Flow.DEVICE);
        assertContextError("mycontext2", "both flow and client-ref set");
        clientRefContext.setFlow(null);

        clientRefContext.setClientRef("nosuch");
        assertContextError("mycontext2", "client-ref=nosuch not found in issuer-ref=myissuer");
    }

    @Test
    public void userMissingForPassword() {
        context.setFlow(Flow.PASSWORD);
        context.setUser(null);
        assertContextError("mycontext", "user required for flow=password");
        context.setUser("asdf");
        context.setUserPassword(null);
        assertContextError("mycontext", "user-password required for flow=password");
    }

    @Test
    public void userSetForDevice() {
        context.setFlow(Flow.DEVICE);
        context.setUser("asdf");
        assertContextError("mycontext", "user set for flow=device");
        context.setUser(null);
        context.setUserPassword("pass");
        assertContextError("mycontext", "user-password set for flow=device");
    }

    @Test
    public void flowMissingForContext() {
        context.setFlow(null);
        assertContextError("mycontext", "missing flow");
    }

    private void assertIssuerError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("issuer=myissuer invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertContextError(String expectedContextId, String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals("context=" + expectedContextId + " invalid: " + expectedMessage, configException.getMessage());
    }

    private void assertError(String expectedMessage) {
        ConfigException configException = Assertions.assertThrows(ConfigException.class, verify());
        Assertions.assertEquals(expectedMessage, configException.getMessage());
    }

    private Executable verify() {
        return () -> ConfigVerifier.verify(config);
    }

}
