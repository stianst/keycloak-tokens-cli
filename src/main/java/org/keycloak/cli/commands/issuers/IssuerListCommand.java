package org.keycloak.cli.commands.issuers;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.stream.Collectors;

@CommandLine.Command(name = "list", description = "List config issuers", mixinStandardHelpOptions = true)
public class IssuerListCommand implements Runnable {

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        interact.println(config.loadConfigFromFile().getIssuers().keySet().stream().sorted().collect(Collectors.joining("  ")));
    }

}
