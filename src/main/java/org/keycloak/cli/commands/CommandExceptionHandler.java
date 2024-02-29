package org.keycloak.cli.commands;

import picocli.CommandLine;

public class CommandExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    private boolean verbose;

    public CommandExceptionHandler() {
        verbose = System.getenv().containsKey("KCT_VERBOSE");
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        commandLine.getErr().println("error");

        if (verbose) {
            e.printStackTrace(commandLine.getErr());
        } else {
            String message = null;
            Throwable current = e;
            while (message == null && e != null) {
                message = e.getMessage();
                current = e.getCause();
            }

            if (message != null) {
                commandLine.getErr().println(message);
            } else {
                commandLine.getErr().println("Exception: " + e.getClass().getName());
            }
        }
        return 1;
    }
}
