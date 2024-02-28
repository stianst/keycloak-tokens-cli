package org.keycloak.cli.tokens;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.keycloak.cli.utils.PrettyPrinterService;

@ApplicationScoped
public class TokenDecoder {

    @Inject
    PrettyPrinterService prettyPrinter;

    public String decode(String token) {
        try {
            JwtClaims claims = new JwtConsumerBuilder()
                    .setSkipSignatureVerification()
                    .setSkipAllValidators()
                    .build()
                    .processToClaims(token);

            return prettyPrinter.prettyPrint(claims.getClaimsMap());
        } catch (InvalidJwtException e) {
            throw new RuntimeException(e);
        }
    }

}
