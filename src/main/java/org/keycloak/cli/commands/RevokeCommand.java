package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.TokenTypeConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.tokens.TokenManagerService;
import picocli.CommandLine;

@CommandLine.Command(name = "revoke", description = "Revoke token", mixinStandardHelpOptions = true)
public class RevokeCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"-t", "--type"}, description = "Token type to revoke", defaultValue = "refresh", converter = TokenTypeConverter.class)
    TokenType tokenType;

    @CommandLine.Option(names = {"--token"}, description = "Token to revoke")
    String token;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        config.setCurrentContext(context);

        boolean revoked = token != null ? tokens.revoke(token) : tokens.revoke(tokenType);

        if (revoked) {
            interact.println("Token revoked");
        } else {
            interact.println("Token not revoked");
        }
    }

}
