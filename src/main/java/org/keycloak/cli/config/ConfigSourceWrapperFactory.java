package org.keycloak.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigSourceWrapperFactory implements ConfigSourceFactory {

    private static final Logger logger = Logger.getLogger(ConfigSourceWrapperFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext configSourceContext) {
        ConfigValue value = configSourceContext.getValue("kct.config.file");

        File configFile = null;
        if (value != null && value.getValue() != null) {
            configFile = new File(value.getValue());
        }

        if (configFile != null && configFile.isFile()) {
            logger.debugv("Wrapping configuration from {0}", value.getValue());
            return Collections.singletonList(new ConfigSourceWrapper(configFile));
        } else {
            return Collections.emptyList();
        }
    }

    public static class ConfigSourceWrapper implements ConfigSource {

        private static final Logger logger = Logger.getLogger(ConfigSourceWrapperFactory.class);

        private Map<String, String> configuration = new HashMap<>();

        public ConfigSourceWrapper(File configFile) {
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            try {
                Config config = om.readValue(configFile, Config.class);
                if (config.getTruststorePath() != null) {
                    configuration.put("kct.truststore", config.getTruststorePath());
                }
                if (config.getTruststorePassword() != null) {
                    configuration.put("kct.truststore.password", config.getTruststorePassword());
                }

                if (logger.isDebugEnabled()) {
                    logger.debugv("Wrapped config keys {0}", String.join(", ", configuration.keySet()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, String> getProperties() {
            return configuration;
        }

        @Override
        public Set<String> getPropertyNames() {
            return configuration.keySet();
        }

        @Override
        public String getValue(String propertyName) {
            return configuration.get(propertyName);
        }

        @Override
        public int getOrdinal() {
            return 200;
        }

        @Override
        public String getName() {
            return "kct config";
        }

    }
}
