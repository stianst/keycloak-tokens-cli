package org.keycloak.cli.commands.issuers;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Create config issuer", mixinStandardHelpOptions = true)
public class IssuerCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--issuer"}, description = "Issuer to create", required = true)
    String issuerId;

    @CommandLine.Option(names = {"-o", "--overwrite"}, description = "Overwrite existing context if it exists")
    boolean overwrite = false;

    @CommandLine.Option(names = {"--url"})
    String url;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();

        if (config.getIssuers().containsKey(issuerId) && !overwrite) {
            throw new RuntimeException("issuer=" + issuerId + " already exists");
        }

        Config.Issuer issuer = new Config.Issuer();
        issuer.setUrl(url);

        config.getIssuers().put(issuerId, issuer);

        ConfigVerifier.verify(config);
        configService.saveConfigToFile(config);

        interact.println("issuer=" + issuerId + " created");
    }

}
