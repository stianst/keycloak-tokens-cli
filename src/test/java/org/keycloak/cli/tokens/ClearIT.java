package org.keycloak.cli.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.container.MockConfigFile;
import org.keycloak.cli.container.MockTokenStoreFile;
import org.keycloak.cli.oidc.Tokens;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@QuarkusMainIntegrationTest
@ExtendWith({MockTokenStoreFile.class, MockConfigFile.class})
@TestProfile(ClearIT.Profile.class)
public class ClearIT {

    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @BeforeEach
    public void addSomeTokens() throws IOException {
        TokenStore tokenStore = new TokenStore();
        for (String i : List.of("mycontext", "mycontext2")) {
            tokenStore.getTokens().put(i, new Tokens("refresh-" + i, Set.of("ref-scope-" + i), "access-" + i, "id-" + i, Set.of("tok-scope-" + i), 1L));
        }
        objectMapper.writeValue(MockTokenStoreFile.tokensFile, tokenStore);
    }

    @Test
    public void clearCurrent(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("clear");
        Assertions.assertEquals("Cleared tokens for context=mycontext", result.getOutput());
        Assertions.assertEquals(Set.of("mycontext2"), listStoredContexts());
    }

    @Test
    public void clearSpecific(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("clear", "-c=mycontext2");
        Assertions.assertEquals("Cleared tokens for context=mycontext2", result.getOutput());
        Assertions.assertEquals(Set.of("mycontext"), listStoredContexts());
    }

    @Test
    public void clearAll(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("clear", "--all");
        Assertions.assertEquals("Cleared all stored tokens", result.getOutput());
        Assertions.assertFalse(MockTokenStoreFile.tokensFile.isFile());
    }

    private Set<String> listStoredContexts() throws IOException {
        return objectMapper.readValue(MockTokenStoreFile.tokensFile, TokenStore.class).getTokens().keySet();
    }


    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath(),
                    "kct.tokens.file", MockTokenStoreFile.tokensFile.getAbsolutePath()
            );
        }

    }

}
