package com.acme.dbo.it.account;

import com.acme.dbo.config.ScreenshotExceptionExtension;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(ScreenshotExceptionExtension.class)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountUatIT {
    @LocalServerPort int serverPort;
    @Autowired WebDriver driver;

    @Test
    public void shouldGetAccountsWhenPrepopulatedDbHasSome() throws IOException {
        driver.get("http://localhost:" + serverPort + "/dbo/swagger-ui.html");
        WebDriverWait wait = new WebDriverWait(driver, 10);

        wait.until(visibilityOfElementLocated(partialLinkText("account-controller")));
        driver.findElement(partialLinkText("account-controller")).click();

        wait.until(visibilityOfElementLocated(linkText("/api/account")));
        driver.findElement(linkText("/api/account")).click();

        wait.until(visibilityOfElementLocated(xpath("//button[text() = 'Try it out ']")));
        driver.findElement(xpath("//button[text() = 'Try it out ']")).click();

        wait.until(visibilityOfElementLocated(xpath("//button[text() = 'Execute']")));
        driver.findElement(xpath("//button[text() = 'Execute']")).click();

        wait.until(visibilityOfElementLocated(xpath("//h4[text() = 'Server response']")));
        assertThat(driver.findElement(xpath("//*[@class = 'response']/*[contains(@class, 'status')]")).getText())
                .isEqualTo("200");
    }

}
