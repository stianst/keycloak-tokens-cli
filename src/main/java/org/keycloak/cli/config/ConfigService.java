package org.keycloak.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class ConfigService {

    @Inject
    StringReplacer stringReplacer;

    @ConfigProperty(name = "kct.config.file")
    private File configFile;

    private Config config;

    private String currentContext;

    private String issuerUrl;

    private Context context;

    public String getContextId() {
        if (config == null) {
            loadConfig();
        }

        String contextId = currentContext != null ? currentContext : config.getDefaultContext();
        if (contextId == null) {
            throw new ConfigException("No context specified, and no default context set");
        }
        return contextId;
    }

    public Context getContext() {
        if (context == null) {
            if (config == null) {
                loadConfig();
            }

            String contextId = getContextId();
            Config.Context context = config.getContexts().get(contextId);
            if (context == null) {
                throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
            }

            String issuerUrl = stringReplacer.replace(context.getIssuer().getUrl());
            this.context = new Context(config.getStoreTokens(), context, issuerUrl);
        }

        return context;
    }

    public void setCurrentContext(String contextId) {
        this.currentContext = contextId;
        this.context = null;
    }

    public Config loadConfig() {
        if (!configFile.isFile() || configFile.length() == 0) {
            config = new Config();
        } else {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            try {
                config = objectMapper.readValue(configFile, Config.class);
            } catch (IOException e) {
                throw new ConfigException("Failed to load config file", e);
            }
            ConfigVerifier.verify(config, stringReplacer);
        }

        return config;
    }

    public void saveConfig(Config config) {
        ConfigVerifier.verify(config, stringReplacer);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            objectMapper.writeValue(configFile, config);
        } catch (IOException e) {
            throw new ConfigException("Failed to save config file", e);
        }
    }

}
