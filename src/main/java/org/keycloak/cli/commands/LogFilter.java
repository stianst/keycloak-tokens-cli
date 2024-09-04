package org.keycloak.cli.commands;

import io.quarkus.logging.LoggingFilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

@LoggingFilter(name = "http-filter")
public class LogFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        if("org.apache.http.wire".equals(record.getLoggerName())) {
            String message = record.getMessage()
                    .trim()
                    .replace("[\\r]", "").replace("[\\n]", "");

            boolean send = message.contains(">>");
            String deliminator = send ? ">>" : "<<";
            message = message.substring(message.indexOf(deliminator) + 4, message.length() - 1);

            if (!message.matches("\\d+")) {
                System.err.println((send ? ">> " : "<< ") + message);
            }

            return false;
        } else {
            return record.getLoggerName().startsWith("org.keycloak");
        }
    }

}
