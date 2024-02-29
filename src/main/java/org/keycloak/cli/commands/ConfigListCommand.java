package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

import java.util.LinkedList;
import java.util.List;

@CommandLine.Command(name = "list", description = "List config contexts", mixinStandardHelpOptions = true)
public class ConfigListCommand implements Runnable {

    @Inject
    ConfigService config;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        List<String> contexts = new LinkedList<>();
        if (config.isConfiguredFromProperties()) {
            contexts.add("<properties>");
        }

        Config c = config.getConfig();
        if (c != null) {
            contexts.addAll(c.getContexts().keySet());
        }

        interact.println(String.join(", ", contexts));
    }

}
