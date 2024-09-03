package org.keycloak.cli.interact;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.cli.config.Messages;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class InteractService {

    private String browserCmd;

    private ObjectWriter yamlWriter;
    private ObjectWriter jsonWriter;

    public InteractService() {
        browserCmd = switch (System.getProperty("os.name")) {
            case "Linux" -> "xdg-open";
            case "Mac OS X" -> "open";
            default -> null;
        };

        yamlWriter = new ObjectMapper(new YAMLFactory()).writerWithDefaultPrettyPrinter();
        jsonWriter = new ObjectMapper(new JsonFactory()).writerWithDefaultPrettyPrinter();
    }

    public void printCreated(Messages.Type type, String id) {
        printMessage(Messages.CREATED, type, id);
    }

    public void printDeleted(Messages.Type type, String id) {
        printMessage(Messages.DELETED, type, id);
    }

    public void printUpdated(Messages.Type type, String id) {
        printMessage(Messages.UPDATED, type, id);
    }

    public void printMessage(String message, Object... arguments) {
        System.out.println(Messages.format(message, arguments));
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void printYaml(String header, Object value) {
        System.out.println(header);
        printYaml(value);
    }

    public void printYaml(Object value) {
        try {
            System.out.println(yamlWriter.writeValueAsString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printJson(Object value) {
        try {
            jsonWriter.writeValue(System.out, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openUrl(URI uri) {
        if (browserCmd == null) {
            throw new RuntimeException("Browser command not set");
        }

        try {
            String[] cmd = new String[]{browserCmd, uri.toString()};
            ProcessBuilder command = new ProcessBuilder().command(cmd);
            Process process = command.start();
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
