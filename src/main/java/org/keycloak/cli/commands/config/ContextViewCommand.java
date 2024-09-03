package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config context", mixinStandardHelpOptions = true)
public class ContextViewCommand implements Runnable {

    @CommandLine.Option(names = {"-a", "--all"}, description = "View all issuers")
    boolean all;

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (all) {
            interact.printYaml(config.getContexts());
        } else {
            if (contextId == null) {
                contextId = config.getDefaultContext();
            }

            Config.Context context = config.getContexts().get(contextId);
            if (context == null) {
                throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
            }
            interact.printYaml(contextId, context);
        }
    }

}
