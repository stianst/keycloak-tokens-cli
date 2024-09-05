package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.commands.converter.TokenTypeConverter;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.kubectl.KubeCtlService;
import org.keycloak.cli.tokens.TokenDecoder;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.utils.JsonFormatter;
import picocli.CommandLine;

import java.util.Set;

@CommandLine.Command(name = "token", description = "Retrieve tokens", mixinStandardHelpOptions = true)
public class TokenCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String context;

    @CommandLine.Option(names = {"-t", "--type"}, description = "Token type to get", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;

    @CommandLine.Option(names = {"-s", "--scope"}, description = "Scope to request", converter = CommaSeparatedListConverter.class)
    Set<String> scope;

    @CommandLine.Option(names = {"-r", "--refresh"}, description = "Force refresh", defaultValue = "false")
    boolean refresh;

    @CommandLine.Option(names = {"-d", "--decode"}, description = "Decode the token", defaultValue = "false")
    boolean decode;

    @CommandLine.Option(names = {"--kubectl"}, description = "Kubectl mode", defaultValue = "false")
    boolean kubectl;

    @Inject
    ConfigService config;

    @Inject
    TokenManagerService tokens;

    @Inject
    InteractService interact;

    @Inject
    JsonFormatter jsonFormatter;

    @Inject
    KubeCtlService kubeCtlService;

    @Override
    public void run() {
        config.setCurrentContext(context);

        if (kubeCtlService.isKubeExecContext()) {
            kubectl = true;
        }

        String token = tokens.getToken(tokenType, scope, refresh);
        if (kubectl) {
            token = kubeCtlService.wrapToken(token);
        } else if (decode) {
            token = jsonFormatter.toPrettyJson(TokenDecoder.decode(token).getClaimsMap());
        }

        interact.println(token);
    }

}
