package org.keycloak.cli.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.enums.Flow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MockConfigFile implements BeforeAllCallback, AfterAllCallback {

    public static final File configFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-config.yaml").toFile();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        Config c = new Config();
        c.setDefaultContext("mycontext");

        Config.Issuer issuer = new Config.Issuer();
        issuer.setUrl("http://issuer1");

        Config.Client client = new Config.Client();
        client.setFlow(Flow.DEVICE);
        client.setId("client1");

        Config.Client client2 = new Config.Client();
        client2.setFlow(Flow.DEVICE);
        client2.setId("client2");

        issuer.setClients(Map.of(
                "client1", client,
                "client2", client2
        ));

        Config.Issuer issuer2 = new Config.Issuer();
        issuer2.setUrl("http://issuer2");

        c.setIssuers(Map.of(
                "issuer1", issuer,
                "issuer2", issuer2
        ));

        Config.Context myContext = new Config.Context();
        myContext.setIssuer("http://issuer");
        myContext.setClient("test-password");
        myContext.setUser("test-user");
        myContext.setUserPassword("test-user-password");
        myContext.setFlow(Flow.PASSWORD);
        myContext.setScope("openid,email");

        Config.Context myContext2 = new Config.Context();
        myContext2.setIssuer("http://myissuer2");
        myContext2.setClient("myclient2");
        myContext2.setFlow(Flow.DEVICE);
        myContext2.setScope("openid2,email2");

        c.setContexts(new HashMap<>());
        c.getContexts().put("mycontext", myContext);
        c.getContexts().put("mycontext2", myContext2);

        try {
            updateCurrent(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config loadCurrent() throws IOException {
        return new ObjectMapper(new YAMLFactory()).readValue(configFile, Config.class);
    }

    public static void updateCurrent(Config config) throws IOException {
        new ObjectMapper(new YAMLFactory()).writeValue(configFile, config);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        configFile.delete();
    }

}