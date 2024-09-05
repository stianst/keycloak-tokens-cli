package org.keycloak.cli.tokens;

import jakarta.inject.Inject;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.keycloak.cli.oidc.OidcService;

import java.security.Key;

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

    public JsonWebSignature verify(String token) {
        try {
            JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(oidcService.keys());
            JwksVerificationKeyResolver jwksVerificationKeyResolver = new JwksVerificationKeyResolver(jsonWebKeySet.getJsonWebKeys());

            JsonWebSignature jws = new JsonWebSignature();
            jws.setCompactSerialization(token);

            Key key = jwksVerificationKeyResolver.resolveKey(jws, null);
            jws.setKey(key);

            return jws;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
