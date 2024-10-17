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
        Config copy = new Config(config.getDefaultContext(), config.getStoreTokens(), new HashMap<>(), config.getTruststore());
        for (Map.Entry<String, Config.Issuer> e : config.getIssuers().entrySet()) {
            copy.getIssuers().put(e.getKey(), resolve(e.getValue()));
        }
        return copy;
    }

    public Config.Issuer resolve(Config.Issuer issuer) {
        return new Config.Issuer(resolve(issuer.getUrl()), issuer.getContexts(), issuer.getClientRegistrationContext());
    }

}
