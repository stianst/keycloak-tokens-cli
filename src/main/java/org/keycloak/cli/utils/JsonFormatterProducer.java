package org.keycloak.cli.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

public class JsonFormatterProducer {

    @ApplicationScoped
    @Produces
    public JsonFormatter createFormatter() {
        return new JsonFormatter();
    }

}
