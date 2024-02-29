package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class ConfigFromPropertiesProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.issuer", "http://localhost:8080/something",
                "kct.flow", "password",
                "kct.client", "test-password",
                "kct.user", "test-user",
                "kct.user-password", "test-user-password",
                "kct.scopes", "openid"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
