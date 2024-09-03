package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.utils.PrettyPrinterService;
import picocli.CommandLine;

import java.util.LinkedHashSet;
import java.util.Set;

@CommandLine.Command(name = "introspect", description = "Introspect token", mixinStandardHelpOptions = true)
public class IntrospectCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"--access-token"}, description = "Optional access token to use")
    String accessToken;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    OidcService oidcService;

    @Inject
    PrettyPrinterService prettyPrinter;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        config.setCurrentContext(context);

        if (accessToken == null) {
            accessToken = tokens.getToken(TokenType.ACCESS, null, false);
        }
        String introspect = oidcService.introspect(accessToken);

        interact.println(prettyPrinter.prettyPrint(introspect));
    }

}
