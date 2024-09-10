package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.HashMap;

@CommandLine.Command(name = "create", description = "Create config context", mixinStandardHelpOptions = true)
public class IssuerCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--iss"}, description = "Issuer to create", required = true)
    String issuerId;

    @CommandLine.Option(names = {"--url"})
    String url;

    @CommandLine.Option(names = {"--client-registration-context"})
    String clientRegistrationContext;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        Config.Issuer issuer = new Config.Issuer(url, new HashMap<>(), clientRegistrationContext);
        if (config.issuers().put(issuerId, issuer) != null) {
            throw ConfigException.exists(Messages.Type.ISSUER, issuerId);
        }

        configService.saveConfig(config);
        interact.printCreated(Messages.Type.ISSUER, issuerId);
    }

}
