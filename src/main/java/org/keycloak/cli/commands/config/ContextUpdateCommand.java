package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Update config context", mixinStandardHelpOptions = true)
public class ContextUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to update")
    String contextId;

    @CommandLine.Option(names = {"--iss"})
    String issuer;

    @CommandLine.Option(names = {"--iss-ref"})
    String issuerRef;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class)
    Flow flow;

    @CommandLine.Option(names = {"--scope"})
    String scope;

    @CommandLine.Option(names = {"--client"})
    String client;

    @CommandLine.Option(names = {"--client-secret"})
    String clientSecret;

    @CommandLine.Option(names = {"--user"})
    String user;

    @CommandLine.Option(names = {"--user-password"})
    String userPassword;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();

        if (contextId == null) {
            contextId = config.getDefaultContext();
        }

        Config.Context context = config.getContexts().get(contextId);

        if (issuer != null) {
            context.getIssuer().setUrl(issuer);
        }
        if (issuerRef != null) {
            context.getIssuer().setRef(null);
        }
        if (flow != null) {
            context.setFlow(flow);
        }
        if (scope != null) {
            context.setScope(scope.split(","));
        }
        if (client != null) {
            context.getClient().setClientId(client);
        }
        if (clientSecret != null) {
            context.getClient().setSecret(clientSecret);
        }
        if (user != null) {
            context.getUser().setUsername(user);
        }
        if (userPassword != null) {
            context.getUser().setPassword(userPassword);
        }

        configService.saveConfig(config);
        interact.printUpdated(Messages.Type.CONTEXT, contextId);
    }

}
