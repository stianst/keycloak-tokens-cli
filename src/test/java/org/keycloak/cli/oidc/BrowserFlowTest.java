package org.keycloak.cli.oidc;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
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

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

@QuarkusTest
@WithTestResource(KeycloakTestResource.class)
@TestProfile(BrowserFlowTest.Profile.class)
@ExtendWith(MockConfigFile.class)
public class BrowserFlowTest {

    @Inject
    TokenService client;

    @Inject
    MockInteractService mockInteractService;

    @Test
    public void token() throws IOException {
        BrowserFlowTest.OpenLink openLink = new BrowserFlowTest.OpenLink();
        openLink.start();

        Tokens token = client.getToken(Collections.emptySet());
        OpenIDAssertions.assertEncodedToken(token.getAccessToken());

        String expectedResponse = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                "    <title>Authenticated</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Authenticated</h1>\n" +
                "<p>Please close window if not closed automatically after 5 seconds</p>\n" +
                "<script>\n" +
                "    setTimeout(window.close, 5000);\n" +
                "</script>\n" +
                "\n" +
                "</body></html>";
        Assertions.assertEquals(expectedResponse, openLink.driver.getPageSource());
    }

    private class OpenLink extends Thread {

        private final WebDriver driver;

        public OpenLink() {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");

            driver = new ChromeDriver(options);
        }

        @Override
        public void run() {
            URI uri;
            try {
                uri = mockInteractService.pollUri(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            
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
