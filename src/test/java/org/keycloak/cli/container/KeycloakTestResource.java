package org.keycloak.cli.container;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = Logger.getLogger(KeycloakTestResource.class);

    static GenericContainer container;

    @Override
    public Map<String, String> start() {
        Config config = ConfigProvider.getConfig();
        ContainerMode containerMode = config.getOptionalValue("kc.container.mode", ContainerMode.class).orElse(ContainerMode.DEFAULT);
        boolean containerLog = config.getOptionalValue("kc.container.log", Boolean.class).orElse(false);

        logger.infov("Starting Keycloak: mode={0}", containerMode);

        return switch (containerMode) {
            case MANUAL -> manual();
            case DEFAULT -> container("keycloak/keycloak:24.0.0", containerLog, "start-dev", "--import-realm");
            case FAST -> container("keycloak-fast-dev", containerLog, "start", "--optimized", "--import-realm");
        };
    }

    private Map<String, String> container(String fullImageName, boolean containerLog, String... commandParts) {
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

            if (containerLog) {
                container.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger(KeycloakTestResource.class)));
            }
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