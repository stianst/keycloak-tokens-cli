package org.keycloak.cli.tokens;

import jakarta.inject.Inject;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.keycloak.cli.oidc.OidcService;

public class TokenDecoder {

    @Inject
    OidcService oidcService;

    public static JwtClaims decode(String token) {
        try {
            return new JwtConsumerBuilder()
                    .setSkipSignatureVerification()
                    .setSkipAllValidators()
                    .build()
                    .processToClaims(token);
        } catch (InvalidJwtException e) {
            throw new RuntimeException(e);
        }
    }

}
