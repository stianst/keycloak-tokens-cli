package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Update config", mixinStandardHelpOptions = true)
public class ConfigUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"--store-tokens"}, description = "Store tokens")
    Boolean storeTokens;

    @CommandLine.Option(names = {"--default-context"}, description = "Set default context")
    String defaultContext;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();

        if (storeTokens != null) {
            config.setStoreTokens(storeTokens);
        }

        if (defaultContext != null) {
            config.setDefaultContext(defaultContext);
        }

        configService.saveConfig(config);
        interact.println("Config updated");
    }

}