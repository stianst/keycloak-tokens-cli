package org.keycloak.cli.oidc;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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

@QuarkusTest
@WithTestResource(KeycloakTestResource.class)
@ExtendWith({ConfigTestProfile.class})
public class DeviceFlowTest {

    @Inject
    ConfigService configService;

    @Inject
    TokenManagerService tokenManagerService;

    @Inject
    MockInteractService mockInteractService;

    @Test
    public void testDeviceFlow() {
        configService.setCurrentContext("test-device");

        OpenLink openLink = new OpenLink();
        openLink.start();

        String accessToken = tokenManagerService.getToken(TokenType.ACCESS, null, false);

        OpenIDAssertions.assertEncodedToken(accessToken);
    }

    private class OpenLink extends Thread {
        @Override
        public void run() {
            String deviceUrl;
            try {
                mockInteractService.poll(60);
                deviceUrl = mockInteractService.poll(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--headless",
                    "--disable-gpu",
                    "--window-size=1920,1200",
                    "--ignore-certificate-errors",
                    "--disable-dev-shm-usage"
            );

            WebDriver driver = new ChromeDriver(options);
            driver.get(deviceUrl);

            driver.findElement(By.id("username")).sendKeys("test-user");
            driver.findElement(By.id("password")).sendKeys("test-user-password");
            driver.findElement(By.id("kc-login")).click();

            driver.findElement(By.id("kc-login")).click();
        }
    }

}
