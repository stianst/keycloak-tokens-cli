package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.oidc.Tokens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
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
        if (tokensFile.isFile()) {
            tokenStore = objectMapper.readValue(tokensFile, TokenStore.class);
            logger.debug("Loading existing token store");
        } else {
            tokenStore = new TokenStore();
        }
    }

    public Tokens getCurrent() {
        return tokenStore.getTokens().get(config.getContext());
    }

    public Map<String, Tokens> getAll() {
        return tokenStore.getTokens();
    }

    public void clearCurrent() throws IOException {
        logger.debugv("Deleting stored tokens for {0}", config.getContext());
        tokenStore.getTokens().remove(config.getContext());
        save();
    }

    public void clearAll() {
        logger.debugv("Deleting stored tokens");
        tokenStore.setTokens(new HashMap<>());
        save();
    }

    public void updateCurrent(Tokens tokens) {
        logger.debugv("Updating stored tokens for {0}", config.getContext());
        tokenStore.getTokens().put(config.getContext(), tokens);
        save();
    }

    private void save() {
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
    }

}
