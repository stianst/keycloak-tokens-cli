package org.keycloak.cli.commands.converter;

import org.keycloak.cli.enums.Flow;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FlowConverter implements CommandLine.ITypeConverter<Flow> {

    @Override
    public Flow convert(String s) {
        try {
            return Flow.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("valid values: " + Arrays.stream(Flow.values()).map(t -> t.toString().toLowerCase()).collect(Collectors.joining(", ")));
        }
    }

}
