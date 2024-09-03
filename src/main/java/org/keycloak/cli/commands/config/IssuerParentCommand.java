package org.keycloak.cli.commands.config;

import picocli.CommandLine;

@CommandLine.Command(name = "issuer", description = "Manage issuers", subcommands = {
        IssuerCreateCommand.class,
        IssuerDeleteCommand.class,
        IssuerViewCommand.class
})
public class IssuerParentCommand {

}