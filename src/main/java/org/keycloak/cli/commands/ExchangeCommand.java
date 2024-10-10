package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenDecoder;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.utils.JsonFormatter;
import picocli.CommandLine;

import java.util.Map;
import java.util.Set;

@CommandLine.Command(name = "exchange", description = "Retrieve tokens", mixinStandardHelpOptions = true)
public class ExchangeCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"--audience"}, description = "Target audience", converter = CommaSeparatedListConverter.class)
    Set<String> audience;

    @CommandLine.Option(names = {"--scope"}, description = "Scope to request", converter = CommaSeparatedListConverter.class, arity = "0..1")
    Set<String> scope;

    @CommandLine.Option(names = {"--requested-token-type"}, description = "Requested token type")
    String requestedTokenType;

    @CommandLine.Option(names = {"--subject-token"}, description = "Subject token")
    String subjectToken;

    @CommandLine.Option(names = {"--subject-token-type"}, description = "Subject token type")
    String subjectTokenType;

    @CommandLine.Option(names = {"--actor-token"}, description = "Actor token")
    String actorToken;

    @CommandLine.Option(names = {"--actor-token-type"}, description = "Actor token type")
    String actorTokenType;

    @CommandLine.Option(names = {"-p", "param"}, description = "Set non-standard parameters on exchange request")
    Map<String, String> params;

    @CommandLine.Option(names = {"-d", "--decode"}, description = "Decode the token", defaultValue = "false")
    boolean decode;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    OidcService oidcService;

    @Inject
    InteractService interact;

    @Inject
    JsonFormatter jsonFormatter;

    @Override
    public void run() {
        config.setCurrentContext(context);

        requestedTokenType = expandTokenType(requestedTokenType);
        subjectTokenType = expandTokenType(subjectTokenType);
        actorTokenType = expandTokenType(actorTokenType);

        if (subjectToken == null) {
            subjectToken = tokens.getToken(TokenType.ACCESS, null, false);
            subjectTokenType = "urn:ietf:params:oauth:token-type:access_token";
        }

        String exchangedToken = oidcService.exchange(audience, scope, requestedTokenType, subjectToken, subjectTokenType, actorToken, actorTokenType, params);

        if (decode) {
            exchangedToken = jsonFormatter.toPrettyJson(TokenDecoder.decode(exchangedToken).getClaimsMap());
        }

        interact.println(exchangedToken);
    }

    private String expandTokenType(String tokenType) {
        return tokenType != null && tokenType.indexOf(':') == -1 ? "urn:ietf:params:oauth:token-type:" + tokenType : tokenType;
    }

}
