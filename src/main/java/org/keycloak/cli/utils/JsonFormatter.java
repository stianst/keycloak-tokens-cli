package org.keycloak.cli.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonFormatter {

    private final ObjectReader reader;
    private final ObjectWriter writer;

    public JsonFormatter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        reader = objectMapper.reader();
        writer = objectMapper.writer(defaultPrettyPrinter);
    }

    public String prettyPrint(Object value) {
        try {
            return writer.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String prettyPrint(String value) {
        try {
            return writer.writeValueAsString(reader.readTree(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
