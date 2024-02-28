package org.keycloak.cli.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrettyPrinterService {

    ObjectWriter writer;

    public PrettyPrinterService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        writer = objectMapper.writer(defaultPrettyPrinter);
    }

    public String prettyPrint(Object value) {
        try {
            return writer.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
