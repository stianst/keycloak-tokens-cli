package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config context", mixinStandardHelpOptions = true)
public class IssuerViewCommand implements Runnable {

    @CommandLine.Option(names = {"-a", "--all"}, description = "View all issuers")
    boolean all;

    @CommandLine.Option(names = {"-i", "--iss"}, description = "Issuer to view")
    String issuerId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (all) {
            interact.printYaml(config.getIssuers());
        } else {
            Config.Issuer issuer = config.getIssuers().get(issuerId);
            if (issuer == null) {
                throw ConfigException.notFound(Messages.Type.ISSUER, issuerId);
            }
            interact.printYaml(issuerId, issuer);
        }
    }

}