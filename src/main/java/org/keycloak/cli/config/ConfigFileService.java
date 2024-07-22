package org.keycloak.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.cli.enums.Flow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConfigFileService {

    @ConfigProperty(name = "kct.config.file")
    File configFile;

    Config config;

    @PostConstruct
    void init() throws IOException {
        config = loadConfigFromFile();
    }

    public Config getConfig() {
        return config;
    }

    public Config loadConfigFromFile() {
        if (configFile.isFile()) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            try {
                return objectMapper.readValue(configFile, Config.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public void saveConfigToFile(Config config) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            objectMapper.writeValue(configFile, config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
