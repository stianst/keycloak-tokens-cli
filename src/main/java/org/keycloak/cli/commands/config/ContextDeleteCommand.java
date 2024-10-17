package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Delete config context", mixinStandardHelpOptions = true)
public class ContextDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to delete", required = true)
    String contextId;

    @Inject
    ConfigService configService;

    @Inject
    OidcService oidcService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        Config.Issuer issuer = null;
        Config.Context removedContext = null;
        for (Config.Issuer i : config.getIssuers().values()) {
            removedContext = i.getContexts().remove(contextId);
            if (removedContext != null) {
                issuer = i;
                break;
            }
        }

        if (removedContext == null) {
            throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
        }

        Config.Client client = removedContext.getClient();
        if (client != null && client.getRegistrationToken() != null && client.getRegistrationUrl() != null) {
            configService.setCurrentContext(issuer.getClientRegistrationContext());
            oidcService.deleteClient(client.getRegistrationToken(), client.getRegistrationUrl());
        }

        configService.saveConfig(config);
        interact.printDeleted(Messages.Type.CONTEXT, contextId);
    }

}
