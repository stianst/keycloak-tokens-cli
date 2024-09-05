package org.keycloak.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.test.junit.QuarkusTestProfile;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.tokens.TokenStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ConfigTestProfile implements QuarkusTestProfile, BeforeAllCallback {

    public static File CONFIG_FILE;
    public static File TOKENS_FILE;
    public static ObjectMapper OBJECT_MAPPER;
    public static TokenStore EMPTY_STORE;
    public static Config DEFAULT_CONFIG;

    static {
        CONFIG_FILE = createTempFile("kct-test-config.yaml");
        TOKENS_FILE = createTempFile("kct-test-tokens.yaml");
        OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
        EMPTY_STORE = new TokenStore();
        try {
            DEFAULT_CONFIG = OBJECT_MAPPER.readValue(ConfigTestProfile.class.getResourceAsStream("example-config.yaml"), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        OBJECT_MAPPER.writeValue(CONFIG_FILE, DEFAULT_CONFIG);
        new FileOutputStream(TOKENS_FILE).close();
    }

    public static Config loadConfig() throws IOException {
        return OBJECT_MAPPER.readValue(CONFIG_FILE, Config.class);
    }

    public static TokenStore loadTokens() throws IOException {
        return OBJECT_MAPPER.readValue(TOKENS_FILE, TokenStore.class);
    }

    public static void updateConfig(Config config) throws IOException {
        OBJECT_MAPPER.writeValue(CONFIG_FILE, config);
    }

    public static void updateTokens(TokenStore tokenStore) throws IOException {
        OBJECT_MAPPER.writeValue(TOKENS_FILE, tokenStore);
    }

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.config.file", CONFIG_FILE.getAbsolutePath(),
                "kct.tokens.file", TOKENS_FILE.getAbsolutePath()
        );
    }

    private static File createTempFile(String name) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = new File(tmpdir, name);
        file.deleteOnExit();
        return file;
    }

}