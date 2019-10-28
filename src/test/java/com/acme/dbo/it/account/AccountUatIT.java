package com.acme.dbo.it.account;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import static lombok.AccessLevel.PRIVATE;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountUatIT {
    @LocalServerPort int serverPort;
    @Autowired WebDriver driver;

    @Test
    public void shouldGetAccountsWhenPrepopulatedDbHasSome() {
        driver.get("http://localhost:" + serverPort + "/dbo/swagger-ui.html");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("base-url")));
        driver.findElement(By.xpath("//a[@href='#/account-controller']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='#/operations/account-controller/getAccountsUsingGET']")));
        driver.findElement(By.xpath("//a[@href='#/operations/account-controller/getAccountsUsingGET']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='operations-account-controller-getAccountsUsingGET']/div[2]/div/div[2]/div[1]/div[2]/button")));
        driver.findElement(By.xpath("//*[@id='operations-account-controller-getAccountsUsingGET']/div[2]/div/div[2]/div[1]/div[2]/button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='operations-account-controller-getAccountsUsingGET']/div[2]/div/div[3]/button")));
        driver.findElement(By.xpath("//*[@id='operations-account-controller-getAccountsUsingGET']/div[2]/div/div[3]/button")).click();
    }

}
