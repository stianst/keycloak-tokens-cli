package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Update config context", mixinStandardHelpOptions = true)
public class IssuerUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--iss"}, description = "Issuer to update", required = true)
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
        Config.Issuer issuer = config.issuers().get(issuerId);
        if (issuer == null) {
            throw ConfigException.notFound(Messages.Type.CONTEXT, issuerId);
        }

        if (url == null) {
            url = issuer.url();
        }
        if (clientRegistrationContext == null) {
            clientRegistrationContext = issuer.clientRegistrationContext();
        }

        config.issuers().remove(issuerId);
        config.issuers().put(issuerId, new Config.Issuer(url, issuer.contexts(), clientRegistrationContext));

        configService.saveConfig(config);
        interact.printUpdated(Messages.Type.ISSUER, issuerId);
    }

}