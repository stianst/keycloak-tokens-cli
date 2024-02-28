package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OpenIDClientService;
import org.keycloak.cli.oidc.UserInfo;
import org.keycloak.cli.tokens.TokenService;
import org.keycloak.cli.utils.PrettyPrinterService;
import picocli.CommandLine;

@CommandLine.Command(name = "userinfo", description = "Retrieve userinfo")
public class UserInfoCommand implements Runnable {

    @Inject
    TokenService tokens;

    @Inject
    OpenIDClientService openIDClient;

    @Inject
    PrettyPrinterService prettyPrinter;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        String accessToken = tokens.getToken(TokenType.ACCESS);
        UserInfo userInfo = openIDClient.getUserInfo(accessToken);

        interact.println(prettyPrinter.prettyPrint(userInfo));
    }

}
