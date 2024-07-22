package org.keycloak.cli.commands.clients;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Delete config issuer", mixinStandardHelpOptions = true)
public class ClientDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer", required = true)
    String issuerId;

    @CommandLine.Option(names = {"--client"}, description = "Client to delete", required = true)
    String clientId;

    @Inject
    ConfigFileService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        config.getIssuers().get(issuerId).getClients().remove(clientId);
        ConfigVerifier.verify(config);

        configService.saveConfigToFile(config);

        interact.println("client=" + clientId + " deleted from issuer=" + issuerId);
    }

}
