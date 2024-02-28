package org.keycloak.cli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.assertion.OpenIDAssertions;
import org.keycloak.cli.container.DeviceProfile;
import org.keycloak.cli.container.KeycloakTestResource;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.mock.MockInteractService;
import org.keycloak.cli.oidc.TokenService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@TestProfile(DeviceProfile.class)
public class DeviceFlowTest {

    @Inject
    TokenService client;

    @Inject
    MockInteractService mockInteractService;

    @Test
    public void token() {
        OpenLink openLink = new OpenLink();
        openLink.start();

        String token = client.getToken(TokenType.ACCESS);
        OpenIDAssertions.assertEncodedToken(token);
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
            options.addArguments("--headless=new");

            WebDriver driver = new ChromeDriver(options);
            driver.get(deviceUrl);

            driver.findElement(By.id("username")).sendKeys("test-user");
            driver.findElement(By.id("password")).sendKeys("test-user-password");
            driver.findElement(By.id("kc-login")).click();

            driver.findElement(By.id("kc-login")).click();
        }
    }

}
