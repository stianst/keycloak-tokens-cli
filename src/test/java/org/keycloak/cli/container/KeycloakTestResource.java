package org.keycloak.cli.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.keycloak.cli.ConfigTestProfile;

import java.util.Map;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return Map.of(
                "keycloak.url", "http://localhost:8080",
                "kct.config.file", ConfigTestProfile.getInstance().getConfigFile().getAbsolutePath(),
                "kct.tokens.file", ConfigTestProfile.getInstance().getTokensFile().getAbsolutePath()
        );
    }

    @Override
    public void stop() {
    }

}