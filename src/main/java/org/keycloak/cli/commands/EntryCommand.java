package org.keycloak.cli.commands;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.keycloak.cli.commands.clients.ClientCommand;
import org.keycloak.cli.commands.context.ContextCommand;
import org.keycloak.cli.commands.issuers.IssuerCommand;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(subcommands = {
        TokenCommand.class,
        DecodeCommand.class,
        ExchangeCommand.class,
        UserInfoCommand.class,
        RevokeCommand.class,
        ClearCommand.class,
        ContextCommand.class,
        IssuerCommand.class,
        ClientCommand.class
}, mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class EntryCommand implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        CommandLine commandLine = new CommandLine(this, factory);
        commandLine.setExecutionExceptionHandler(new CommandExceptionHandler());
        return commandLine.execute(args);
    }

}
