package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.FilePermissionUtils;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.oidc.Tokens;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Base64;
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
        tokenStore.setProviderMetadata(new HashMap<>());
        save();
    }

    public void updateCurrent(Tokens tokens) {
        logger.debugv("Updating stored tokens for {0}", config.getContextId());
        tokenStore.getTokens().put(config.getContextId(), tokens);
        save();
    }

    public OIDCProviderMetadata getProviderMetadata() {
        String issuer = config.getIssuer();
        String s = tokenStore.getProviderMetadata().get(issuer);
        if (s != null) {
            String decoded = new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8);
            try {
                return OIDCProviderMetadata.parse(decoded);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void updateProviderMetadata(OIDCProviderMetadata providerMetadata) {
        String s = providerMetadata.toString();
        String encoded = new String(Base64.getUrlEncoder().encode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        tokenStore.getProviderMetadata().put(config.getIssuer(), encoded);
        save();
    }

    public void clearProviderMetadata(String issuer) {
        tokenStore.getProviderMetadata().remove(issuer);
        save();
    }

    private void save() {
        if (!tokenStore.getTokens().isEmpty() || !tokenStore.getProviderMetadata().isEmpty()) {
            try {
                boolean newFile = !tokensFile.isFile();
                if (newFile) {
                    File configDir = tokensFile.getParentFile();
                    if (!configDir.isDirectory()) {
                        if (!configDir.mkdirs()) {
                            throw new ConfigException("Failed to create config directory");
                        }
                    }
                }

                objectMapper.writeValue(tokensFile, tokenStore);
                if (newFile) {
                    FilePermissionUtils.userOnlyPermissions(tokensFile);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            tokensFile.delete();
        }
    }

}
