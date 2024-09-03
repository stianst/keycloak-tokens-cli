package org.keycloak.cli.commands.config;

import picocli.CommandLine;

@CommandLine.Command(name = "config", description = "Configure", subcommands = {
        ContextParentCommand.class,
        IssuerParentCommand.class,
        ConfigUpdateCommand.class,
        ConfigViewCommand.class
}, mixinStandardHelpOptions = true)
public class ConfigParentCommand {

}