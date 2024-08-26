package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.tokens.TokenDecoderService;
import picocli.CommandLine;

@CommandLine.Command(name = "decode", description = "Retrieve tokens", mixinStandardHelpOptions = true)
public class DecodeCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Token to decode")
    String token;

    @Inject
    TokenDecoderService tokenDecoder;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        token = tokenDecoder.decode(token);
        interact.println(token);
    }

}
