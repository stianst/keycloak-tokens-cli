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

public class MockEmptyConfigFile implements BeforeAllCallback, AfterAllCallback {

    public static final File configFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-config.yaml").toFile();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
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