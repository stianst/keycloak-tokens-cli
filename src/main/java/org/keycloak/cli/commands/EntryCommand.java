package org.keycloak.cli.commands;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(subcommands = {
        TokenCommand.class,
        UserInfoCommand.class,
        ClearCommand.class,
        ConfigCommand.class
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
