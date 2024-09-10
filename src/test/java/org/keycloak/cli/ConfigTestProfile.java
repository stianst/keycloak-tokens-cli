package org.keycloak.cli;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.tokens.TokenStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ConfigTestProfile implements BeforeAllCallback {

    private static final ConfigTestProfile instance = new ConfigTestProfile();

    private File configFile;
    private File tokensFile;
    private ObjectMapper objectMapper;
    private TokenStore emptyStore;
    private Config defaultConfig;

    public ConfigTestProfile() {
        configFile = createTempFile("kct-test-config.yaml");
        tokensFile = createTempFile("kct-test-tokens.yaml");
        objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configOverride(Map.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        try {
            defaultConfig = objectMapper.readValue(ConfigTestProfile.class.getResourceAsStream("example-config.yaml"), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigTestProfile getInstance() {
        return instance;
    }

    public Config getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        objectMapper.writeValue(configFile, defaultConfig);
        new FileOutputStream(tokensFile).close();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getTokensFile() {
        return tokensFile;
    }

    public Config loadConfig() throws IOException {
        return objectMapper.readValue(configFile, Config.class);
    }

    public TokenStore loadTokens() throws IOException {
        return objectMapper.readValue(tokensFile, TokenStore.class);
    }

    public void updateConfig(Config config) throws IOException {
        objectMapper.writeValue(configFile, config);
    }

    public void updateTokens(TokenStore tokenStore) throws IOException {
        objectMapper.writeValue(tokensFile, tokenStore);
    }

    private static File createTempFile(String name) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = new File(tmpdir, name);
        file.deleteOnExit();
        return file;
    }

}