package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.TokenService;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class PasswordFlowTest {

    @Inject
    TokenService client;

    @Test
    public void token() {
        String token = client.getToken(TokenType.ACCESS);
        OpenIDAssertions.assertEncodedToken(token);
    }

}
