package org.keycloak.cli.container;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class DeviceProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "kct.issuer", "${keycloak.issuer}",
                "kct.flow", "device",
                "kct.client", "test-device",
                "kct.client-secret", "",
                "kct.scope", "openid"
        );
    }

    @Override
    public String getConfigProfile() {
        return "test";
    }
}
