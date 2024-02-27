package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    static GenericContainer container;

    @Override
    public Map<String, String> start() {
        ContainerMode containerMode = ConfigProvider.getConfig().getValue("keycloak.container.mode", ContainerMode.class);
        if (containerMode == null) {
            containerMode = ContainerMode.MANUAL;
        }
        Map<String, String> config = switch (containerMode) {
            case MANUAL -> manual();
            case DEFAULT -> container("keycloak/keycloak", "start-dev", "--import-realm");
            case FAST_DEV -> container("keycloak-fast-dev", "start", "--optimized", "--import-realm");
        };
        return config;
    }

    private Map<String, String> container(String fullImageName, String... commandParts){
        if (container == null) {
            byte[] testrealm;
            try {
                testrealm = getClass().getResourceAsStream("/testrealm.json").readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            container = new GenericContainer<>(DockerImageName.parse(fullImageName))
                    .withExposedPorts(8080)
                    .withAccessToHost(true)
                    .withCommand(commandParts)
                    .withCopyToContainer(Transferable.of(testrealm), "/opt/keycloak/data/import/testrealm.json");

            container.start();
//            this.container.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(KeycloakTestResource.class)));
        }

        String url = "http://" + container.getHost() + ":" + container.getMappedPort(8080);

        return Map.of(
                "keycloak.url", url,
                "keycloak.issuer", url + "/realms/test"
        );
    }

    private Map<String, String> manual() {
        String url = "http://localhost:8080";
        return Map.of(
            "keycloak.url", url,
            "keycloak.issuer", url + "/realms/test"
        );
    }

    @Override
    public void stop() {
//        keycloak.stop();
    }
}