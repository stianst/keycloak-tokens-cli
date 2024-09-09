package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.Set;

@CommandLine.Command(name = "update", description = "Update config context", mixinStandardHelpOptions = true)
public class ContextUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to update")
    String contextId;

    @CommandLine.Option(names = {"--iss"}, arity = "0..1")
    String iss;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class)
    Flow flow;

    @CommandLine.Option(names = {"--scope"}, converter = CommaSeparatedListConverter.class, arity = "0..1")
    Set<String> scope;

    @CommandLine.Option(names = {"--client"})
    String client;

    @CommandLine.Option(names = {"--client-secret"}, arity = "0..1")
    String clientSecret;

    @CommandLine.Option(names = {"--user"}, arity = "0..1")
    String user;

    @CommandLine.Option(names = {"--user-password"}, arity = "0..1")
    String userPassword;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (contextId == null) {
            contextId = config.defaultContext();
        }

        Config.Issuer issuer = config.issuers().values().stream().filter(i -> i.contexts().containsKey(contextId)).findFirst().orElse(null);
        if (issuer == null) {
            throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
        }
        Config.Context context = issuer.contexts().get(contextId);

        if (flow == null) {
            flow = context.flow();
        }
        if (scope == null) {
            scope = context.scope();
        }
        if (client == null) {
            client = context.client() != null ? context.client().clientId() : null;
        }
        if (clientSecret == null) {
            clientSecret = context.client() != null ? context.client().secret() : null;
        }
        if (user == null) {
            user = context.user() != null ? context.user().username() : null;
        }
        if (userPassword != null) {
            userPassword = context.user() != null ? context.user().password() : null;
        }

        issuer.contexts().remove(contextId);

        if (iss != null) {
            issuer = config.issuers().get(iss);
        }

        issuer.contexts().put(contextId, new Config.Context(
                flow,
                client != null ? new Config.Client(client, clientSecret) : null,
                user != null ? new Config.User(user, userPassword) : null,
                scope
        ));

        configService.saveConfig(config);
        interact.printUpdated(Messages.Type.CONTEXT, contextId);
    }

}
