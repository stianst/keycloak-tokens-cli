package org.keycloak.cli.commands.converter;

import org.keycloak.cli.enums.TokenType;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TokenTypeConverter implements CommandLine.ITypeConverter<TokenType> {
    @Override
    public TokenType convert(String s) {
        try {
            return TokenType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("valid values: " + Arrays.stream(TokenType.values()).map(t -> t.toString().toLowerCase()).collect(Collectors.joining(", ")));
        }
    }
}
