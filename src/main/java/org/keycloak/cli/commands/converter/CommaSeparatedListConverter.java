package org.keycloak.cli.commands.converter;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CommaSeparatedListConverter implements CommandLine.ITypeConverter<Set<String>> {

    @Override
    public Set<String> convert(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
