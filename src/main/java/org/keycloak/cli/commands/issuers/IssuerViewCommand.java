package org.keycloak.cli.commands.issuers;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.stream.Collectors;

@CommandLine.Command(name = "view", description = "View config issuer", mixinStandardHelpOptions = true)
public class IssuerViewCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer to use", required = true)
    String issuerId;

    @Inject
    ConfigFileService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        Config.Issuer issuer = config.getIssuers().get(issuerId);

        interact.println("url=" + issuer.getUrl());
        if (!issuer.getClients().isEmpty()) {
            interact.println("clients=" + issuer.getClients().keySet().stream().sorted().collect(Collectors.joining("  ")));
        }
    }

}
