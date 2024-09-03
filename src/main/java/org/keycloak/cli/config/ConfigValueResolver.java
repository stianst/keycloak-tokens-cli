package org.keycloak.cli.config;

import java.util.Map;

public class ConfigValueResolver {

    private final Config config;

    private ConfigValueResolver(Config config) {
        this.config = config;
    }

    public static void resolve(Config config) {
        new ConfigValueResolver(config).resolve();
    }

    public void resolve() {
        Map<String, Config.Issuer> issuers = config.getIssuers();
        if (issuers != null && !issuers.isEmpty()) {
            for (Config.Context c : config.getContexts().values()) {
                Config.Issuer i = c.getIssuer();
                if (i != null) {
                    i = issuers.get(i.getRef());
                    c.setIssuer(i);
                }
            }
        }
    }

}
