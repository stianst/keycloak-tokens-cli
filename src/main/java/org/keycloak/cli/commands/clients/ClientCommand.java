package org.keycloak.cli.commands.clients;

import picocli.CommandLine;

@CommandLine.Command(name = "client", subcommands = {
        ClientListCommand.class,
        ClientViewCommand.class,
        ClientCreateCommand.class,
        ClientDeleteCommand.class
})
public class ClientCommand {
}
