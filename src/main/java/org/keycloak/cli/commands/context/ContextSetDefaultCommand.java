package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigFileService;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "set-default", description = "Set default config context", mixinStandardHelpOptions = true)
public class ContextSetDefaultCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to set as default", required = true)
    String contextId;

    @Inject
    ConfigFileService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        config.setDefaultContext(contextId);
        ConfigVerifier.verify(config);

        configService.saveConfigToFile(config);
    }

}
