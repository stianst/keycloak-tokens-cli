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

@CommandLine.Command(name = "create", description = "Create config context", mixinStandardHelpOptions = true)
public class ContextCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to create", required = true)
    String contextId;

    @CommandLine.Option(names = {"--iss"})
    String issuer;

    @CommandLine.Option(names = {"--iss-ref"})
    String issuerRef;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class, required = true)
    Flow flow;

    @CommandLine.Option(names = {"--scope"}, converter = CommaSeparatedListConverter.class)
    Set<String> scope;

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
        Config.Context context = new Config.Context(
                new Config.Issuer(issuer, issuerRef),
                flow,
                client != null ? new Config.Client(client, clientSecret) : null,
                user != null ? new Config.User(user, userPassword) : null,
                scope
        );
        if (config.getContexts().put(contextId, context) != null) {
            throw ConfigException.exists(Messages.Type.CONTEXT, contextId);
        }
        if (config.getDefaultContext() != null) {
            config.setDefaultContext(contextId);
        }

        configService.saveConfig(config);
        interact.printCreated(Messages.Type.CONTEXT, contextId);
    }

}
