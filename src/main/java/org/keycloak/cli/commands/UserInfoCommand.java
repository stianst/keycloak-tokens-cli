package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.TokenService;
import org.keycloak.cli.oidc.UserInfo;
import org.keycloak.cli.oidc.UserInfoService;
import org.keycloak.cli.utils.PrettyPrinterService;
import picocli.CommandLine;

@CommandLine.Command(name = "userinfo", description = "Retrieve userinfo")
public class UserInfoCommand implements Runnable {

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
        String accessToken = tokens.getToken(TokenType.ACCESS);
        UserInfo userInfo = userInfoService.getUserInfo(accessToken);

        interact.println(prettyPrinter.prettyPrint(userInfo));
    }

}
