package org.keycloak.cli.oidc;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.mock.MockInteractService;
import org.keycloak.cli.tokens.TokenManagerService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;

@QuarkusTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith({ConfigTestProfile.class})
public class BrowserFlowTest {

    @Inject
    ConfigService configService;

    @Inject
    TokenManagerService tokenManagerService;

    @Inject
    MockInteractService mockInteractService;

    @Test
    public void testBrowserFlow() {
        configService.setCurrentContext("test-browser");

        BrowserFlowTest.OpenLink openLink = new BrowserFlowTest.OpenLink();
        openLink.start();

        String accessToken = tokenManagerService.getToken(TokenType.ACCESS, null, false);
        OpenIDAssertions.assertEncodedToken(accessToken);

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

}
