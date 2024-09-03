package org.keycloak.cli.commands.config;

import picocli.CommandLine;

@CommandLine.Command(name = "context", description = "Manage contexts", subcommands = {
        ContextViewCommand.class,
        ContextCreateCommand.class,
        ContextUpdateCommand.class,
        ContextDeleteCommand.class
}, mixinStandardHelpOptions = true)
public class ContextParentCommand {

}