package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config", mixinStandardHelpOptions = true)
public class ConfigViewCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        if (context != null) {
            config.setContext(context);
        }

        interact.println("context: " + config.getContext());
        interact.println("issuer: " + config.getIssuer());
        interact.println("client: " + config.getClient());
        if (config.getClientSecret() != null) {
            interact.println("client-secret: " + config.getClientSecret().replaceAll(".", "*"));
        }
        if (config.getUser() != null) {
            interact.println("user: " + config.getUser());
        }
        if (config.getUserPassword() != null) {
            interact.println("user-password: " + config.getUserPassword().replaceAll(".", "*"));
        }
        if (config.getScope() != null) {
            interact.println("scope: " + String.join(",", config.getScope()));
        }
    }

}
