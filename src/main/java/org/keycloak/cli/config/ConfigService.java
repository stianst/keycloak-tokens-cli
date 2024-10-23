package org.keycloak.cli.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ConfigService {

    @Inject
    VariableResolver variableResolver;

    @ConfigProperty(name = "kct.config.file")
    private File configFile;

    private Config config;

    private String currentContext;

    private String issuer;

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

    public String getIssuer() {
        getContext();
        return issuer;
    }

    public Context getContext() {
        if (context == null) {
            if (config == null) {
                loadConfig();
            }

            String contextId = getContextId();
            Config.Issuer issuer = null;
            Config.Context context = null;
            for (Map.Entry<String, Config.Issuer> i : config.getIssuers().entrySet()) {
                context = i.getValue().getContexts().get(contextId);
                if (context != null) {
                    issuer = i.getValue();
                    this.issuer = i.getKey();
                    break;
                }
            }

            if (context == null) {
                throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
            }

            String issuerUrl = variableResolver.resolve(issuer.getUrl());
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
            config = new Config(null, false, new HashMap<>(), null);
        } else {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            objectMapper.configOverride(Map.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
            try {
                config = objectMapper.readValue(configFile, Config.class);
            } catch (IOException e) {
                throw new ConfigException("Failed to load config file", e);
            }
            ConfigVerifier.verify(config, variableResolver);
        }
        return config;
    }

    public void saveConfig(Config config) {
        boolean creatingFile = !configFile.isFile();

        if (creatingFile) {
            File configDir = configFile.getParentFile();
            if (!configDir.isDirectory()) {
                if (!configDir.mkdirs()) {
                    throw new ConfigException("Failed to create config directory");
                }
            }
        }

        ConfigVerifier.verify(config, variableResolver);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            objectMapper.writeValue(configFile, config);
        } catch (IOException e) {
            throw new ConfigException("Failed to save config file", e);
        }

        if (creatingFile) {
            FilePermissionUtils.userOnlyPermissions(configFile);
        }
    }

}
