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

                    if (c.getClientRef() != null) {
                        Config.Client client = issuer.getClients().get(c.getClientRef());
                        c.setClient(client.getId());
                        c.setClientSecret(client.getSecret());
                        c.setFlow(client.getFlow());
                    }
                }
            }
        }
    }

}
