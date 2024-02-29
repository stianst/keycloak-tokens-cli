package org.keycloak.cli.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.enums.Flow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

@Singleton
@Startup
public class MockConfigFile {

    public MockConfigFile() {
        File configFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct.yaml").toFile();
        Config c = new Config();
        c.setDefaultContext("mycontext");

        Config.Context myContext = new Config.Context();
        myContext.setIssuer("http://myissuer");
        myContext.setClient("myclient");
        myContext.setUser("myuser");
        myContext.setUserPassword("myuserPassword");
        myContext.setFlow(Flow.DEVICE);
        myContext.setScope("openid,email");

        Config.Context myContext2 = new Config.Context();
        myContext2.setIssuer("http://myissuer2");
        myContext2.setClient("myclient2");
        myContext2.setUser("myuser2");
        myContext2.setFlow(Flow.PASSWORD);
        myContext2.setScope("openid2,email2");

        c.setContexts(new HashMap<>());
        c.getContexts().put("mycontext", myContext);
        c.getContexts().put("mycontext2", myContext2);

        try {
            new ObjectMapper(new YAMLFactory()).writeValue(configFile, c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        configFile.deleteOnExit();
    }

}
