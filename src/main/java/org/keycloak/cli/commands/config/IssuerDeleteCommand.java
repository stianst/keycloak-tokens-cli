package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.tokens.TokenStoreService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Create config context", mixinStandardHelpOptions = true)
public class IssuerDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--iss"}, description = "Issuer to delete", required = true)
    String issuerId;

    @CommandLine.Option(names = {"--force"}, description = "Delete issuer and contexts")
    boolean force;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Inject
    TokenStoreService tokenStoreService;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        Config.Issuer removed = config.getIssuers().remove(issuerId);
        if (removed == null) {
            throw ConfigException.notFound(Messages.Type.ISSUER, issuerId);
        }

        if (!force && !removed.getContexts().isEmpty()) {
            throw new ConfigException(Messages.format("Issuer ''{0}'' contains contexts, please delete contexts first or use --force", issuerId));
        }

        if (config.getDefaultContext() != null && removed.getContexts().containsKey(config.getDefaultContext())) {
            config.setDefaultContext(null);
        }

        tokenStoreService.clearProviderMetadata(issuerId);

        configService.saveConfig(config);
        interact.printDeleted(Messages.Type.ISSUER, issuerId);
    }
}