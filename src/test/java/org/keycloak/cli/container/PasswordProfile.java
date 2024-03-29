package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class PasswordProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.issuer", "${keycloak.issuer}",
                "kct.flow", "password",
                "kct.client", "test-password",
                "kct.user", "test-user",
                "kct.user-password", "test-user-password",
                "kct.scope", "openid"
        );
    }

}
