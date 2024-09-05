package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
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
    String issuer;

    @CommandLine.Option(names = {"--iss-ref"}, arity = "0..1")
    String issuerRef;

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
            contextId = config.getDefaultContext();
        }

        Config.Context context = config.getContexts().get(contextId);

        if (issuer != null) {
            context.getIssuer().setUrl(issuer.isBlank() ? null : issuer);
        }
        if (issuerRef != null) {
            context.getIssuer().setRef(issuerRef.isBlank() ? null : issuerRef);
        }
        if (flow != null) {
            context.setFlow(flow);
        }
        if (scope != null) {
            context.setScope(scope.isEmpty() ? null : scope);
        }
        if (client != null) {
            context.getClient().setClientId(client.isBlank() ? null : client);
        }
        if (clientSecret != null) {
            context.getClient().setSecret(clientSecret.isBlank() ? null : clientSecret);
        }
        if (user != null) {
            context.getUser().setUsername(user.isBlank() ? null : user);
        }
        if (userPassword != null) {
            context.getUser().setPassword(userPassword.isBlank() ? null : userPassword);
        }
        if (context.getUser().getUsername() == null && context.getUser().getPassword() == null) {
            context.setUser(null);
        }

        configService.saveConfig(config);
        interact.printUpdated(Messages.Type.CONTEXT, contextId);
    }

}
