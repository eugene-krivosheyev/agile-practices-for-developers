package com.acme.dbo.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestConfig {
    @Lazy @Bean
    @Profile("it")
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(new ChromeOptions()
                .addArguments("--headless")
                .addArguments("--disable-gpu")
                .addArguments("--start-maximized"));

        driver.manage().window().setSize(new Dimension(800, 2_000));
        return driver;
    }
}
