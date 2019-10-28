package com.acme.dbo.it.account;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountUatIT {
    @LocalServerPort int serverPort;
    @Autowired WebDriver driver;

    @Test
    public void shouldGetAccountsWhenPrepopulatedDbHasSome() throws IOException {
        try {
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

        } catch (org.openqa.selenium.NotFoundException e) {
            driver.manage().window().maximize();
            TakesScreenshot screenshotable = (TakesScreenshot) this.driver;
            copy(
                    screenshotable.getScreenshotAs(OutputType.FILE).toPath(),
                    Paths.get("target", LocalDateTime.now().toString() + ".jpg"),
                    REPLACE_EXISTING
            );

            throw e;
        }


    }

}
