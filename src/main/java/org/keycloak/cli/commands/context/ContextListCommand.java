package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.stream.Collectors;

@CommandLine.Command(name = "list", description = "List config contexts", mixinStandardHelpOptions = true)
public class ContextListCommand implements Runnable {

    @Inject
    ConfigFileService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        interact.println(config.loadConfigFromFile().getContexts().keySet().stream().sorted().collect(Collectors.joining("  ")));
    }

}
