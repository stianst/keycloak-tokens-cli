package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.commands.converter.TokenTypeConverter;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.tokens.TokenDecoder;
import org.keycloak.cli.tokens.Tokens;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "token", description = "Retrieve tokens")
public class TokenCommand implements Runnable {

    @CommandLine.Option(names = "--decode", description = "Decode the token", defaultValue = "false")
    boolean decode;

    @CommandLine.Option(names = "--type", description = "Token type to get", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;

    @CommandLine.Option(names = "--scope", description = "Scope to request", converter = CommaSeparatedListConverter.class)
    List<String> scope;

    @Inject
    Tokens tokens;

    @Inject
    TokenDecoder tokenDecoder;

    @Override
    public void run() {
        String token = tokens.getToken(tokenType, scope);

        if (decode) {
            token = tokenDecoder.decode(token);
        }

        System.out.println(token);
    }

}
