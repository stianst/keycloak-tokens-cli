package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.OpenIDClient;
import org.keycloak.cli.oidc.UserInfo;
import org.keycloak.cli.tokens.Tokens;
import org.keycloak.cli.utils.PrettyPrinter;
import picocli.CommandLine;

@CommandLine.Command(name = "userinfo", description = "Retrieve userinfo")
public class UserInfoCommand implements Runnable {

    @Inject
    Tokens tokens;

    @Inject
    OpenIDClient openIDClient;

    @Inject
    PrettyPrinter prettyPrinter;

    @Override
    public void run() {
        String accessToken = tokens.getToken(TokenType.ACCESS);
        UserInfo userInfo = openIDClient.getUserInfo(accessToken);

        System.out.println(prettyPrinter.prettyPrint(userInfo));
    }

}
