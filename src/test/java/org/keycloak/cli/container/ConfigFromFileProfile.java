package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class ConfigFromFileProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.config.file", "${java.io.tmpdir}/test-kct.yaml"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
