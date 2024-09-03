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

public class ConfigFileSmallryeSourceFactory implements ConfigSourceFactory {

    private static final Logger logger = Logger.getLogger(ConfigFileSmallryeSourceFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext configSourceContext) {
        ConfigValue value = configSourceContext.getValue("kct.config.file");
        if (value != null && value.getValue() != null) {
            File configFile = new File(value.getValue());

            if (configFile.isFile() && configFile.length() > 0) {
                logger.tracev("Wrapping configuration from {0}", value.getValue());
                return Collections.singletonList(new ConfigSourceWrapper(configFile));
            }
        }

        return Collections.emptyList();
    }

    public static class ConfigSourceWrapper implements ConfigSource {

        private final Map<String, String> configuration = new HashMap<>();

        public ConfigSourceWrapper(File configFile) {
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            try {
                Config config = om.readValue(configFile, Config.class);
                Config.Truststore truststore = config.getTruststore();
                if (truststore != null) {
                    if (truststore.getPath() != null) {
                        configuration.put("kct.truststore", truststore.getPath());
                    }
                    if (truststore.getPassword() != null) {
                        configuration.put("kct.truststore.password", truststore.getPassword());
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.tracev("Wrapped config keys {0}", String.join(", ", configuration.keySet()));
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