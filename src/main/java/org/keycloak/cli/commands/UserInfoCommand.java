package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.utils.JsonFormatter;
import picocli.CommandLine;

import java.util.LinkedHashSet;
import java.util.Set;

@CommandLine.Command(name = "userinfo", description = "Retrieve userinfo", mixinStandardHelpOptions = true)
public class UserInfoCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"-s", "--scope"}, description = "Scope to request", converter = CommaSeparatedListConverter.class)
    Set<String> scope;

    @CommandLine.Option(names = {"--access-token"}, description = "Optional access token to use")
    String accessToken;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    OidcService oidcService;

    @Inject
    JsonFormatter jsonFormatter;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        config.setCurrentContext(context);

        Set<String> scope = this.scope != null ? new LinkedHashSet<>(this.scope) : new LinkedHashSet<>();
        if (!scope.contains("openid")) {
            scope.add("openid");
        }

        if (accessToken == null) {
            accessToken = tokens.getToken(TokenType.ACCESS, scope, false);
        }
        String userInfo = oidcService.userInfo(accessToken);

        interact.println(jsonFormatter.toPrettyJson(userInfo));
    }

}
