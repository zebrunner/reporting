package com.qaprosoft.zafira.tests.gui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserProfilePage extends BasePage {

    @FindBy(xpath="//button[ancestor::form[@name='access_token_form'] and @type='submit']")
    private WebElement generateTokenButton;

    @FindBy(xpath="//button[ancestor::form[@name='access_token_form'] and @type='button']")
    private WebElement copyTokenButton;

    @FindBy(xpath="//input[@name='accessToken']")
    private WebElement tokenInput;

    @FindBy(xpath="//div[contains(@class, 'ajs-success')]")
    private WebElement copyActionAlert;

    public UserProfilePage(WebDriver driver)
    {
        super(driver, "/users/profile");
    }

    public void generateToken() {
        this.waitUntilElementIsPresent(generateTokenButton,3);
        generateTokenButton.click();
        this.waitUntilElementIsPresent(copyTokenButton,1);
        }

    public boolean copyToken() {
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//header/div[contains(@class, 'profile-img')]")));
        generateTokenButton.click();
        new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[ancestor::form[@name='access_token_form'] and @type='button']")));
        copyTokenButton.click();
        return copyActionAlert != null;
    }

    public WebElement getTokenInput() {
        return tokenInput;
    }
}
