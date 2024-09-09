package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Delete config context", mixinStandardHelpOptions = true)
public class ContextDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to delete", required = true)
    String contextId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        boolean removed = false;
        for (Config.Issuer issuer : config.issuers().values()) {
            if (issuer.contexts().remove(contextId) != null) {
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
        }

        configService.saveConfig(config);
        interact.printDeleted(Messages.Type.CONTEXT, contextId);
    }

}
