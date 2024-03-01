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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class MockTokenStoreResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = Logger.getLogger(MockTokenStoreResource.class);

    private static final File tokensFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-tokens.yaml").toFile();

    @Override
    public Map<String, String> start() {
        logger.debug("Setting kct.tokens.file to " + tokensFile.getAbsolutePath());
        if (tokensFile.isFile()) {
            tokensFile.delete();
        }
        return Map.of("kct.tokens.file", tokensFile.getAbsolutePath());
    }

    @Override
    public void stop() {
        logger.debug("Deleting test kct.tokens.file");
        tokensFile.delete();
    }
}