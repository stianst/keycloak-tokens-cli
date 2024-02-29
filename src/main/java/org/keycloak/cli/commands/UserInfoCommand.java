package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.TokenService;
import org.keycloak.cli.oidc.UserInfo;
import org.keycloak.cli.oidc.UserInfoService;
import org.keycloak.cli.utils.PrettyPrinterService;
import picocli.CommandLine;

import java.util.LinkedList;
import java.util.List;

@CommandLine.Command(name = "userinfo", description = "Retrieve userinfo", mixinStandardHelpOptions = true)
public class UserInfoCommand implements Runnable {

    @CommandLine.Option(names = { "-s", "--scope" }, description = "Scope to request", converter = CommaSeparatedListConverter.class)
    List<String> scope;

    @Inject
    TokenService tokens;

    @Inject
    UserInfoService userInfoService;

    @Inject
    PrettyPrinterService prettyPrinter;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        List<String> scope = this.scope != null ? new LinkedList<>(this.scope) : new LinkedList<>();
        if (!scope.contains("openid")) {
            scope.add("openid");
        }

        String accessToken = tokens.getToken(TokenType.ACCESS, scope);
        UserInfo userInfo = userInfoService.getUserInfo(accessToken);

        interact.println(prettyPrinter.prettyPrint(userInfo));
    }

}
