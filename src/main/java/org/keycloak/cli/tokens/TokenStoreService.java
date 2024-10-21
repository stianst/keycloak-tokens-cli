package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.FileUtils;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.Tokens;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class TokenStoreService {

    private static final Logger logger = Logger.getLogger(TokenStoreService.class);

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @ConfigProperty(name = "kct.tokens.file")
    String tokensFilePath;

    File tokensFile;

    @Inject
    ConfigService config;

    TokenStore tokenStore;

    @PostConstruct
    public void init() throws IOException {
        tokensFile = new File(this.tokensFilePath);
        if (tokensFile.isFile() && tokensFile.length() > 0) {
            tokenStore = objectMapper.readValue(tokensFile, TokenStore.class);
            logger.debug("Loading existing token store");
        } else {
            tokenStore = new TokenStore();
        }
    }

    public Tokens getCurrent() {
        return tokenStore.getTokens().get(config.getContextId());
    }

    public Map<String, Tokens> getAll() {
        return tokenStore.getTokens();
    }

    public void clearCurrent() {
        logger.debugv("Deleting stored tokens for {0}", config.getContextId());
        tokenStore.getTokens().remove(config.getContextId());
        save();
    }

    public void clear(String contextId) {
        logger.debugv("Deleting stored tokens for {0}", contextId);
        tokenStore.getTokens().remove(contextId);
        save();
    }

    public void clearCurrent(TokenType tokenType) {
        logger.debugv("Deleting stored {0} token for {1}", tokenType.name().toLowerCase(Locale.ENGLISH), config.getContextId());
        switch (tokenType) {
            case REFRESH:
                tokenStore.getTokens().remove(config.getContextId());
                break;
            case ACCESS:
                getCurrent().setAccessToken(null);
                break;
            case ID:
                getCurrent().setIdToken(null);

        }
        save();
    }

    public void clearAll() {
        logger.debugv("Deleting stored tokens");
        tokenStore.setTokens(new HashMap<>());
        save();
    }

    public void updateCurrent(Tokens tokens) {
        logger.debugv("Updating stored tokens for {0}", config.getContextId());
        tokenStore.getTokens().put(config.getContextId(), tokens);
        save();
    }

    private void save() {
        boolean creatingFile = !tokensFile.isFile();
        if (creatingFile) {
            File configDir = tokensFile.getParentFile();
            if (!configDir.isDirectory()) {
                if (!configDir.mkdirs()) {
                    throw new ConfigException("Failed to create config directory");
                }
            }
        }

        if (!tokenStore.getTokens().isEmpty()) {
            try {
                boolean newFile = !tokensFile.isFile();
                objectMapper.writeValue(tokensFile, tokenStore);
                if (newFile) {
                    try {
                        Files.setPosixFilePermissions(tokensFile.toPath(), PosixFilePermissions.fromString("rw-------"));
                    } catch (UnsupportedOperationException e) {
                        tokensFile.setReadable(true, true);
                        tokensFile.setWritable(true, true);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            tokensFile.delete();
        }

        if (creatingFile) {
            FileUtils.userOnlyPermissions(tokensFile);
        }
    }

}
