package org.keycloak.cli.commands;

import io.quarkus.logging.LoggingFilter;
import org.keycloak.cli.utils.JsonFormatter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

@LoggingFilter(name = "http-filter")
public class LogFilter implements Filter {

    private JsonFormatter jsonFormatter = new JsonFormatter();

    @Override
    public boolean isLoggable(LogRecord record) {
        if("org.apache.http.wire".equals(record.getLoggerName())) {
            String message = record.getMessage()
                    .trim()
                    .replace("[\\r]", "").replace("[\\n]", "");

            boolean send = message.contains(">>");
            String deliminator = send ? ">>" : "<<";
            message = message.substring(message.indexOf(deliminator) + 4, message.length() - 1);

            if (message.startsWith("{") && message.endsWith("}")) {
                System.out.println(deliminator + deliminator);
                for (String l : jsonFormatter.prettyPrint(message).split("\n")) {
                    System.err.println(l);
                }
                System.out.println(deliminator + deliminator);
            } else if (message.contains("=") && message.contains("&")) {
                System.out.println(deliminator + deliminator);
                for (String l : message.split("&")) {
                    System.err.println(URLDecoder.decode(l, StandardCharsets.UTF_8));
                }
                System.out.println(deliminator + deliminator);
            } else if (message.length() > 5) {
                System.err.println(deliminator + " " + message);
            }

            return false;
        } else {
            return record.getLoggerName().startsWith("org.keycloak");
        }
    }

}
