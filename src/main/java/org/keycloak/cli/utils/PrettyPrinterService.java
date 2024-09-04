package org.keycloak.cli.utils;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrettyPrinterService {

    private final JsonFormatter jsonFormatter;

    public PrettyPrinterService() {
        jsonFormatter = new JsonFormatter();
    }

    public String prettyPrint(Object value) {
        return jsonFormatter.prettyPrint(value);
    }

    public String prettyPrint(String value) {
        return jsonFormatter.prettyPrint(value);
    }

}
