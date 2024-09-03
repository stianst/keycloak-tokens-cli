package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenDecoderService;
import org.keycloak.cli.tokens.TokenManagerService;
import picocli.CommandLine;

import java.util.Set;

@CommandLine.Command(name = "exchange", description = "Retrieve tokens", mixinStandardHelpOptions = true)
public class ExchangeCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"-d", "--decode"}, description = "Decode the token", defaultValue = "false")
    boolean decode;

    @CommandLine.Option(names = {"-a", "--audience"}, description = "Target audience", converter = CommaSeparatedListConverter.class)
    Set<String> audience;

    @CommandLine.Option(names = {"-s", "--scope"}, description = "Scope to request", converter = CommaSeparatedListConverter.class)
    Set<String> scope;

    @CommandLine.Option(names = {"-st", "--subject-token"}, description = "Subject token")
    String subjectToken;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    OidcService oidcService;

    @Inject
    TokenDecoderService tokenDecoder;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        config.setCurrentContext(context);

        if (subjectToken == null) {
            subjectToken = tokens.getToken(TokenType.ACCESS, scope, false);
        }

        String exchangedToken = oidcService.exchange(subjectToken, audience, scope);

        if (decode) {
            exchangedToken = tokenDecoder.decode(exchangedToken);
        }

        interact.println(exchangedToken);
    }

}
