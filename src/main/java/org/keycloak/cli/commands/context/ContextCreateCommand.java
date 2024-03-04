package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Create config context", mixinStandardHelpOptions = true)
public class ContextCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to create", required = true)
    String contextId;

    @CommandLine.Option(names = {"-o", "--overwrite"}, description = "Overwrite existing context if it exists")
    boolean overwrite = false;

    @CommandLine.Option(names = {"--issuer-ref"})
    String issuerRef;

    @CommandLine.Option(names = {"--issuer"})
    String issuer;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class)
    Flow flow;

    @CommandLine.Option(names = {"--scope"})
    String scope;

    @CommandLine.Option(names = {"--client-ref"})
    String clientRef;

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
        Config config = configService.loadConfigFromFile();

        if (config.getContexts().containsKey(contextId) && !overwrite) {
            throw new RuntimeException("context=" + contextId + " already exists");
        }

        Config.Context context = new Config.Context();
        context.setIssuer(issuer);
        context.setIssuerRef(issuerRef);

        context.setScope(scope);

        context.setFlow(flow);

        context.setClient(client);
        context.setClientRef(clientRef);
        context.setClientSecret(clientSecret);

        context.setUser(user);
        context.setUserPassword(userPassword);

        config.getContexts().put(contextId, context);

        ConfigVerifier.verify(config);
        configService.saveConfigToFile(config);

        interact.println("context=" + contextId + " created");
    }

}
