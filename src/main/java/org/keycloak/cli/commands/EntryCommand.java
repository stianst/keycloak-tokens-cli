package org.keycloak.cli.commands;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.Logger;
import org.keycloak.cli.commands.config.ConfigParentCommand;
import picocli.CommandLine;

import java.util.logging.Level;

@QuarkusMain
@CommandLine.Command(subcommands = {
        TokenCommand.class,
        DecodeCommand.class,
        IntrospectCommand.class,
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

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Verbose output", defaultValue = "false", scope = CommandLine.ScopeType.INHERIT)
    public void setVerbose(boolean verbose) {
        verbose = verbose || System.getenv().containsKey("KCT_VERBOSE");

        if (verbose) {
            CommandExceptionHandler.setVerbose(verbose);

            Logger logger = LogContext.getLogContext().getLogger("org.keycloak.cli");
            logger.setLevel(Level.ALL);
        }
    }

    @Override
    public int run(String... args) throws Exception {
        CommandLine commandLine = new CommandLine(this, factory);
        commandLine.setExecutionExceptionHandler(new CommandExceptionHandler());
        return commandLine.execute(args);
    }

}
