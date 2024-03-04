package org.keycloak.cli.commands.issuers;

import picocli.CommandLine;

@CommandLine.Command(name = "issuer", subcommands = {
        IssuerListCommand.class,
        IssuerViewCommand.class,
        IssuerCreateCommand.class,
        IssuerDeleteCommand.class
})
public class IssuerCommand {
}
