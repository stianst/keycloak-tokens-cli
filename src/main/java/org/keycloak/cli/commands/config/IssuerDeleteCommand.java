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

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer to delete", required = true)
    String issuerId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (config.getIssuers().remove(issuerId) == null) {
            throw ConfigException.notFound(Messages.Type.ISSUER, issuerId);
        }

        configService.saveConfig(config);
        interact.printDeleted(Messages.Type.ISSUER, issuerId);
    }
}