package org.keycloak.cli.commands;

import picocli.CommandLine;

@CommandLine.Command(name = "config", subcommands = {
        ConfigViewCommand.class,
        ConfigListCommand.class
})
public class ConfigCommand {

}