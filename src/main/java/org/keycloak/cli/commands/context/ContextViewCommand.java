package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config context", mixinStandardHelpOptions = true)
public class ContextViewCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        if (contextId == null) {
            contextId = config.getDefaultContext();
        }

        Config.Context context = config.getContexts().get(contextId);
        Config.Issuer issuerRef = context.getIssuerRef() != null ? config.getIssuers().get(context.getIssuerRef()) : null;
        Config.Client clientRef = context.getClientRef() != null ? issuerRef.getClients().get(context.getClientRef()) : null;

        if (issuerRef != null) {
            interact.println("issuer-ref=" + context.getIssuerRef());
            interact.println(" > url=" + issuerRef.getUrl());
        } else {
            interact.println("issuer=" + context.getIssuer());
        }

        interact.println("");

        if (clientRef != null) {
            interact.println("client-ref=" + context.getClientRef());
            interact.println(" >> flow=" + clientRef.getFlow().jsonName());
            interact.println(" >> client=" + clientRef.getId());
            if (context.getClientSecret() != null) {
                interact.println(" >> client-secret=" + clientRef.getSecret());
            }
        } else {
            interact.println("flow=" + context.getFlow().jsonName());
            interact.println("client=" + context.getClient());
            if (context.getClientSecret() != null) {
                interact.println("client-secret=" + context.getClientSecret().replaceAll(".", "*"));
            }
        }

        if (context.getScope() != null) {
            interact.println("");
            interact.println("scope=" + String.join(",", context.getScope()));
        }


        if (context.getUser() != null) {
            interact.println("");
            interact.println("user=" + context.getUser());
            if (context.getUserPassword() != null) {
                interact.println("user-password=" + context.getUserPassword().replaceAll(".", "*"));
            }
        }
    }

}
