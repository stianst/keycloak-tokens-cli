package org.keycloak.cli.commands.context;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.ConfigVerifier;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Delete context", mixinStandardHelpOptions = true)
public class ContextDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to delete", required = true)
    String contextId;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfigFromFile();
        ConfigVerifier.verify(config);

        config.getContexts().remove(contextId);
        ConfigVerifier.verify(config);

        configService.saveConfigToFile(config);

        interact.println("context=" + contextId + " deleted");
    }

}
