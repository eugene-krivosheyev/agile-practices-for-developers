package com.acme.dbo.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestConfig {
    @Lazy @Bean
    @Profile("it")
    public WebDriver webDriver() {
        WebDriverManager.phantomjs().setup();
        return new PhantomJSDriver();
    }
}
