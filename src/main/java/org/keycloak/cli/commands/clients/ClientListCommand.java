package org.keycloak.cli.commands.clients;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.stream.Collectors;

@CommandLine.Command(name = "list", description = "List config issuers", mixinStandardHelpOptions = true)
public class ClientListCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer to use", required = true)
    String issuerId;

    @Inject
    ConfigFileService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        interact.println(config.loadConfigFromFile().getIssuers().get(issuerId).getClients().keySet().stream().sorted().collect(Collectors.joining("  ")));
    }

}
