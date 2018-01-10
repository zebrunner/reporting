package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserProfileTest extends AbstractTest {

    private static UserProfilePage userProfilePage;

    @FindBy(xpath="//button[@type='submit']")
    private WebElement loginButton;

    @BeforeMethod
    public void loginUser()
    {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        DashboardPage dashboardPage = loginPage.login(ADMIN1_USER, ADMIN1_PASS);

        userProfilePage = dashboardPage.goToUserProfilePage();
        //userProfilePage.waitUntilPageIsLoaded(2);
    }

    @Test
    public void testGenerateToken() {
        userProfilePage.generateToken();
        Assert.assertFalse(userProfilePage.getTokenInput().getAttribute("value").isEmpty());
    }

    @Test
    public void testCopyToken() {
        Assert.assertTrue(userProfilePage.copyToken());
    }
}
