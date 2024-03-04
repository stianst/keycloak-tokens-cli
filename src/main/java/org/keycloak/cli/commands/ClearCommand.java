package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.tokens.TokenStoreService;
import picocli.CommandLine;

@CommandLine.Command(name = "clear", description = "Clear stored tokens", mixinStandardHelpOptions = true)
public class ClearCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"--all"}, description = "Clear all stored tokens", defaultValue = "false")
    boolean all;

    @Inject
    ConfigService config;

    @Inject
    TokenStoreService tokenStoreService;

    @Inject
    InteractService interactService;

    @Override
    public void run() {
        if (all) {
            tokenStoreService.clearAll();

            interactService.println("Cleared all stored tokens");
        } else {
            if (context != null) {
                config.setContext(context);
            }

            tokenStoreService.clearCurrent();
            interactService.println("Cleared tokens for context=" + config.getContext());
        }
    }

}
