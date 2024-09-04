package org.keycloak.cli.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class VariableResolver {

    private StringSubstitutor stringSubstitutor;

    @PostConstruct
    public void init() {
        Map<String, String> valueMap = new HashMap<>();
        ConfigProvider.getConfig().getPropertyNames().forEach(p -> valueMap.put(p, ConfigProvider.getConfig().getConfigValue(p).getValue()));
        stringSubstitutor = new StringSubstitutor(valueMap);
    }

    public void init(Map<String, Object> valueMap) {
        stringSubstitutor = new StringSubstitutor(valueMap);
    }

    public String resolve(String value) {
        return stringSubstitutor.replace(value);
    }

    public Config resolve(Config config) {
        if (config.getIssuers() != null) {
            for (Config.Issuer issuer : config.getIssuers().values()) {
                resolve(issuer);
            }
        }
        if (config.getContexts() != null) {
            for (Config.Context context : config.getContexts().values()) {
                resolve(context);
            }
        }
        return config;
    }

    public Config.Context resolve(Config.Context context) {
        resolve(context.getIssuer());
        return context;
    }

    public Config.Issuer resolve(Config.Issuer issuer) {
        if (issuer != null && issuer.getUrl() != null) {
            issuer.setUrl(resolve(issuer.getUrl()));
        }
        return issuer;
    }

}
