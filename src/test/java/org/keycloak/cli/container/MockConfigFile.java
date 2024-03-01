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

public class MockConfigFile implements BeforeAllCallback, AfterAllCallback {

    public static final File configFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-config.yaml").toFile();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        Config c = new Config();
        c.setDefaultContext("mycontext");

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
            new ObjectMapper(new YAMLFactory()).writeValue(configFile, c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        configFile.delete();
    }

}