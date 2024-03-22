package org.keycloak.cli.oidc;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.PasswordProfile;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.tokens.TokenManagerService;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(PasswordProfile.class)
public class PasswordFlowTest {

    @Inject
    TokenManagerService tokens;

    @Test
    public void token() {
        String token = tokens.getToken(TokenType.ACCESS, null, false);
        OpenIDAssertions.assertEncodedToken(token);
    }

}
