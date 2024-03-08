package org.keycloak.cli.commands.clients;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Create config client", mixinStandardHelpOptions = true)
public class ClientCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer ref", required = true)
    String issuerId;

    @CommandLine.Option(names = {"-c", "--client"}, description = "Client ref", required = true)
    String clientRef;

    @CommandLine.Option(names = {"--id"}, description = "The client id", required = true)
    String clientId;

    @CommandLine.Option(names = {"--secret"}, description = "The client secret")
    String clientSecret;

    @CommandLine.Option(names = {"--flow"}, description = "The flow to use for the client", required = true, converter = FlowConverter.class)
    Flow flow;

    @CommandLine.Option(names = {"-o", "--overwrite"}, description = "Overwrite existing client if it exists")
    boolean overwrite = false;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();

        if (config.getIssuers().get(issuerId).getClients().containsKey(clientRef) && !overwrite) {
            throw new RuntimeException("client=" + issuerId + " already exists for issuer=" + issuerId);
        }

        Config.Client client = new Config.Client();
        client.setId(clientId);
        client.setSecret(clientSecret);
        client.setFlow(flow);

        config.getIssuers().get(issuerId).getClients().put(clientRef, client);

        ConfigVerifier.verify(config);
        configService.saveConfigToFile(config);

        interact.println("client=" + clientRef + " created in issuer=" + issuerId);
    }

}
