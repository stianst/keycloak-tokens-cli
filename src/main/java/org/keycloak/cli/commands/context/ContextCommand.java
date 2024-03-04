package org.keycloak.cli.commands.context;

import picocli.CommandLine;

@CommandLine.Command(name = "context", subcommands = {
        ContextViewCommand.class,
        ContextListCommand.class,
        ContextViewDefaultCommand.class,
        ContextSetDefaultCommand.class,
        ContextCreateCommand.class,
        ContextDeleteCommand.class
})
public class ContextCommand {

}