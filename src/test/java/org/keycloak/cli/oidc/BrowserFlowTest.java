package org.keycloak.cli.oidc;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.container.MockConfigFile;
import org.keycloak.cli.mock.MockInteractService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(BrowserFlowTest.Profile.class)
@ExtendWith(MockConfigFile.class)
public class BrowserFlowTest {

    @Inject
    TokenService client;

    @Inject
    MockInteractService mockInteractService;

    @Test
    public void token() {
        BrowserFlowTest.OpenLink openLink = new BrowserFlowTest.OpenLink();
        openLink.start();

        Tokens token = client.getToken(Collections.emptySet());
        OpenIDAssertions.assertEncodedToken(token.getAccessToken());
    }

    private class OpenLink extends Thread {
        @Override
        public void run() {
            URI uri;
            try {
                uri = mockInteractService.pollUri(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");

            WebDriver driver = new ChromeDriver(options);
            driver.get(uri.toString());

            driver.findElement(By.id("username")).sendKeys("test-user");
            driver.findElement(By.id("password")).sendKeys("test-user-password");
            driver.findElement(By.id("kc-login")).click();
        }
    }

    public static class Profile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "kct.issuer", "${keycloak.issuer}",
                    "kct.flow", "browser",
                    "kct.client", "test-browser",
                    "kct.scope", "openid",
                    "kct.config.file", MockConfigFile.configFile.getAbsolutePath()
            );
        }

    }

}
