package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.VariableResolver;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View config", mixinStandardHelpOptions = true)
public class ConfigViewCommand implements Runnable {

    @CommandLine.Option(names = {"-r", "--resolve"}, description = "Resolve variables")
    boolean resolve;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Inject
    VariableResolver variableResolver;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (resolve) {
            config = variableResolver.resolve(config);
        }

        interact.printYaml(config);
    }

}