package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Update config", mixinStandardHelpOptions = true)
public class ConfigUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"--default-context"}, description = "Set default context")
    String defaultContext;

    @CommandLine.Option(names = {"--store-tokens"}, description = "Store tokens")
    Boolean storeTokens;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config old = configService.loadConfig();
        if (storeTokens == null) {
            storeTokens = old.getStoreTokens();
        }
        if (defaultContext == null) {
            defaultContext = old.getDefaultContext();
        }
        Config config = new Config(defaultContext, storeTokens, old.getIssuers(), old.getTruststore());
        configService.saveConfig(config);
        interact.println("Config updated");
    }

}