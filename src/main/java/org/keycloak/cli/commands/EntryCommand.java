package org.keycloak.cli.commands;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.keycloak.cli.commands.config.ConfigParentCommand;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(subcommands = {
        TokenCommand.class,
        DecodeCommand.class,
        ExchangeCommand.class,
        UserInfoCommand.class,
        RevokeCommand.class,
        ClearCommand.class,
        ConfigParentCommand.class
}, mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class EntryCommand implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        CommandLine commandLine = new CommandLine(this, factory);
        boolean verbose = System.getenv().containsKey("KCT_VERBOSE");
        commandLine.setExecutionExceptionHandler(new CommandExceptionHandler(verbose));
        return commandLine.execute(args);
    }

}
