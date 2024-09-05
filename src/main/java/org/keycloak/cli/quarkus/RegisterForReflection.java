package org.keycloak.cli.quarkus;

@io.quarkus.runtime.annotations.RegisterForReflection(targets = {
        org.jose4j.jwt.JwtClaims.class,
        org.jose4j.jwt.NumericDate.class
})
public class RegisterForReflection {

}
