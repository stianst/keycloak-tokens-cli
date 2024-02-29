package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class ConfigFromBothProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.config.file", "${java.io.tmpdir}/test-kct.yaml",
                "kct.issuer", "http://issuer"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
