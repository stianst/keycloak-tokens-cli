package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import picocli.CommandLine;

@CommandLine.Command(name = "config", description = "Show config")
public class ShowConfigCommand implements Runnable {

    @CommandLine.Option(names = { "-c", "--context" }, description = "Context to use")
    String context;

    @Inject
    Config config;

    @Override
    public void run() {
        System.out.println("Showing config");

        if (context != null) {
            config.setContext(context);
        }

        System.out.println(config.getContext());
        System.out.println(config.getIssuer());
        System.out.println(config.getScope());
    }

}
