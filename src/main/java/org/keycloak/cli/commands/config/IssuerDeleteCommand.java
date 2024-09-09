package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Create config context", mixinStandardHelpOptions = true)
public class IssuerDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--iss"}, description = "Issuer to delete", required = true)
    String issuerId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        Config.Issuer removed = config.issuers().remove(issuerId);
        if (removed == null) {
            throw ConfigException.notFound(Messages.Type.ISSUER, issuerId);
        }
        if (config.defaultContext() != null && removed.contexts().containsKey(config.defaultContext())) {
            config = new Config(null, config.storeTokens(), config.issuers(), config.truststore());
        }

        configService.saveConfig(config);
        interact.printDeleted(Messages.Type.ISSUER, issuerId);
    }
}