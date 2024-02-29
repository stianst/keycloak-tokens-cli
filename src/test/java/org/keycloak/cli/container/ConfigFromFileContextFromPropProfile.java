package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class ConfigFromFileContextFromPropProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.config.file", "${java.io.tmpdir}/test-kct.yaml",
                "kct.context", "mycontext2"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
