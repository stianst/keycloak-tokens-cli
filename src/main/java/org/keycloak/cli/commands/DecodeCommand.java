package org.keycloak.cli.commands;

import jakarta.inject.Inject;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.jose4j.lang.UnresolvableKeyException;
import org.keycloak.cli.commands.converter.TokenTypeConverter;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenManagerService;
import org.keycloak.cli.utils.JsonFormatter;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@CommandLine.Command(name = "decode", description = "Decode token", mixinStandardHelpOptions = true)
public class DecodeCommand implements Runnable {

    @CommandLine.Option(names = {"--verify"}, description = "Verify token signature", defaultValue = "false")
    boolean verify;

    @CommandLine.Parameters(index = "0", description = "Token to decode", arity = "0..1")
    String token;

    @CommandLine.Option(names = {"-t", "--type"}, description = "Token type to get", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;

    @CommandLine.Option(names = {"--jwks"}, description = "JSON Web Key Sets")
    URI uri;

    @Inject
    JsonFormatter jsonFormatter;

    @Inject
    TokenManagerService tokenManagerService;

    @Inject
    OidcService oidcService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        try {
            if (token == null) {
                token = tokenManagerService.getToken(tokenType, null, false);
            }
            decode(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void decode(String token) throws JoseException, IOException {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setCompactSerialization(token);

        if (verify) {
            JsonWebKeySet jsonWebKeySet;
            if (uri != null) {
                if (uri.getScheme().equals("file")) {
                    jsonWebKeySet = new JsonWebKeySet(new String(uri.toURL().openStream().readAllBytes(), StandardCharsets.UTF_8));
                } else {
                    jsonWebKeySet = new JsonWebKeySet(oidcService.keys(uri));
                }
            } else {
                jsonWebKeySet = new JsonWebKeySet(oidcService.keys());
            }
            JwksVerificationKeyResolver jwksVerificationKeyResolver = new JwksVerificationKeyResolver(jsonWebKeySet.getJsonWebKeys());


            try {
                Key key = jwksVerificationKeyResolver.resolveKey(jws, null);
                jws.setKey(key);
            } catch (UnresolvableKeyException e) {
            }
        }

        interact.println("Header");
        interact.println("------");
        interact.println(jsonFormatter.toPrettyJson(jws.getHeaders().getFullHeaderAsJsonString()));
        interact.println("");

        interact.println("Payload");
        interact.println("-------");

        interact.println(jsonFormatter.toPrettyJson(jws.getUnverifiedPayload()));
        interact.println("");

        try {
            if (jws.getKey() != null) {
                interact.println("Verify signature: " + jws.verifySignature());
            } else {
                interact.println("Verify signature: key not found");
            }
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }

}
