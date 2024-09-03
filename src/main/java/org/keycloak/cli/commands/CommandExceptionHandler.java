package org.keycloak.cli.commands;

import picocli.CommandLine;

public class CommandExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    private final boolean verbose;

    public CommandExceptionHandler(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        if (verbose) {
            e.printStackTrace(commandLine.getErr());
        } else {
            String message = getDeepestMessage(e);
            if (message != null) {
                commandLine.getErr().println(commandLine.getColorScheme().errorText(message));
            } else {
                commandLine.getErr().println("Exception: " + e.getClass().getName());
            }
        }
        return 1;
    }

    private String getDeepestMessage(Throwable t) {
        String current = t.getMessage();
        while (t.getCause() != null) {
            t = t.getCause();
            if (t.getMessage() != null) {
                current = t.getMessage();
            }
        }
        return current;
    }

}
