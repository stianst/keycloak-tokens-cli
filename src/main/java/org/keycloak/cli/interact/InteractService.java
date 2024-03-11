package org.keycloak.cli.interact;

import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class InteractService {

    private String browserCmd;

    public InteractService() {
        switch (System.getProperty("os.name")) {
            case "Linux":
                browserCmd = "xdg-open";
                break;
            case "Mac OS X":
                browserCmd = "open";
            default:
                browserCmd = null;
        }

    }

    public void println(String message) {
        System.out.println(message);
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
