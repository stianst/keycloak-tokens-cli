package org.keycloak.cli.commands.clients;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config issuer", mixinStandardHelpOptions = true)
public class ClientViewCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer", required = true)
    String issuerId;

    @CommandLine.Option(names = {"-c", "--client"}, description = "Client to delete", required = true)
    String clientId;

    @Inject
    ConfigFileService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        Config.Client client = config.getIssuers().get(issuerId).getClients().get(clientId);

        interact.println("id=" + client.getId());
        interact.println("secret=" + client.getSecret().replaceAll(".", "*"));
        interact.println("flow=" + client.getFlow().jsonName());
    }

}
