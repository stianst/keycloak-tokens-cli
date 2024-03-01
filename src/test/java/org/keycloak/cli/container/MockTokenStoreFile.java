package org.keycloak.cli.container;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.nio.file.Path;

public class MockTokenStoreFile implements BeforeAllCallback, AfterAllCallback {

    private static final Logger logger = Logger.getLogger(MockTokenStoreFile.class);

    public static final File tokensFile = Path.of(System.getProperty("java.io.tmpdir"), "test-kct-tokens.yaml").toFile();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        logger.debug("Setting kct.tokens.file to " + tokensFile.getAbsolutePath());
        if (tokensFile.isFile()) {
            tokensFile.delete();
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        logger.debug("Deleting test kct.tokens.file");
        tokensFile.delete();
    }
}