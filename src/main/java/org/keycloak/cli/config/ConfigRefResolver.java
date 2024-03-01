package org.keycloak.cli.config;

import java.util.Map;

public class ConfigRefResolver {

    private final Config config;

    private ConfigRefResolver(Config config) {
        this.config = config;
    }

    public static void resolve(Config config) {
        new ConfigRefResolver(config).resolve();
    }

    public void resolve() {
        Map<String, Config.Issuer> issuers = config.getIssuers();
        if (issuers != null && !issuers.isEmpty()) {
            for (Config.Context c : config.getContexts().values()) {
                if (c.getIssuerRef() != null) {
                    Config.Issuer issuer = issuers.get(c.getIssuerRef());
                    c.setIssuer(issuer.getUrl());
                }
            }
        }
    }

}
