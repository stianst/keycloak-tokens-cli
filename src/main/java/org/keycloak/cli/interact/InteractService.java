package org.keycloak.cli.interact;

import jakarta.enterprise.context.ApplicationScoped;

import java.net.URL;

@ApplicationScoped
public class InteractService {

    public void println(String message) {
        System.out.println(message);
    }

    public void openUrl(URL url) {
        // TODO
    }

}
