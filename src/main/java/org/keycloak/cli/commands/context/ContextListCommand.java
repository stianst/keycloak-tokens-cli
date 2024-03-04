package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "list", description = "List config contexts", mixinStandardHelpOptions = true)
public class ContextListCommand implements Runnable {

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        interact.println(String.join("  ", config.loadConfigFromFile().getContexts().keySet()));
    }

}
