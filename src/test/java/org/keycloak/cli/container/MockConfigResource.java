package org.keycloak.cli.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.enums.Flow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class MockConfigResource implements QuarkusTestResourceLifecycleManager {

    private static final File configFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-config.yaml").toFile();

    @Override
    public Map<String, String> start() {
        Config config = new Config();
        config.setStoreTokens(true);

        Config.Context context = new Config.Context();
        context.setIssuer("http://issuer");
        context.setFlow(Flow.PASSWORD);
        context.setClient("test-password");
        context.setUser("test-user");
        context.setUserPassword("test-user-password");
        context.setScope("roles,email");

        config.setContexts(Map.of("test-context", context));

        config.setDefaultContext("test-context");

        try {
            new ObjectMapper(new YAMLFactory()).writeValue(configFile, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Map.of("kct.config.file", configFile.getAbsolutePath());
    }

    @Override
    public void stop() {
        configFile.delete();
    }
}