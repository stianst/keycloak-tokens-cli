package org.keycloak.cli.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(subcommands = {
        TokenCommand.class,
        UserInfoCommand.class,
        ShowConfigCommand.class
})
public class EntryCommand {
}
