package com.acme.dbo.it.account.uat.page;

import org.openqa.selenium.WebDriver;

import static org.openqa.selenium.support.PageFactory.initElements;

public class MainPage {
    private WebDriver driver;



    public MainPage(WebDriver driver) {
        this.driver = driver;
        initElements(driver, this);
    }
}
