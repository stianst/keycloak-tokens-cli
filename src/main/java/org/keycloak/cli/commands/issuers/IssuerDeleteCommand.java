package org.keycloak.cli.commands.issuers;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Delete config issuer", mixinStandardHelpOptions = true)
public class IssuerDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"--issuer"}, description = "Issuer to delete", required = true)
    String issuerId;

    @Inject
    ConfigFileService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        config.getIssuers().remove(issuerId);
        ConfigVerifier.verify(config);

        configService.saveConfigToFile(config);

        interact.println("issuer=" + issuerId + " deleted");
    }

}
