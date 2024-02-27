package org.keycloak.cli.commands.converter;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommaSeparatedListConverter implements CommandLine.ITypeConverter<List<String>> {
    @Override
    public List<String> convert(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
