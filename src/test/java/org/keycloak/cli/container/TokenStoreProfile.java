package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class TokenStoreProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.issuer", "dummy"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
