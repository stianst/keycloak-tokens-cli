package org.keycloak.cli.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class StringReplacer {

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

    public String replace(String value) {
        return stringSubstitutor.replace(value);
    }

}
