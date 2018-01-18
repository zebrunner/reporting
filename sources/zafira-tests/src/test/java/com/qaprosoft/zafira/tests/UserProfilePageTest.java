package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.UserProfilePageService;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.qaprosoft.zafira.tests.gui.pages.UserProfilePage.ColorSchema;

public class UserProfilePageTest extends AbstractTest {

    private UserProfilePage userProfilePage;
    private UserProfilePageService userProfilePageService;
    private LoginPageService loginPageService;

    @FindBy(xpath="//button[@type='submit']")
    private WebElement loginButton;

    @BeforeMethod
    public void loginUser()
    {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPageService = new LoginPageService(driver);
        DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
        userProfilePageService = new UserProfilePageService(driver);
        userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
    }

    @Test
    public void testGenerateToken() {
        userProfilePageService.generateToken();
        Assert.assertFalse(userProfilePage.getTokenInput().getAttribute("value").isEmpty());
    }

    @Test
    public void testCopyToken() {
        Assert.assertTrue(userProfilePageService.copyToken());
    }

    @Test
    public void testColorSchemaChange() {
        ColorSchema colorSchema = userProfilePageService.checkCurrentColorSchemeByRadioButton();
        if (colorSchema == ColorSchema.LIGHT){
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePageService.pickDarkSchemaRadioButton();
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
        } else if (colorSchema == ColorSchema.DARK){
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePageService.pickLightSchemaRadioButton();
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
        }
    }

    @Test
    public void testNewColorSchemaSave() {
        ColorSchema colorSchema = userProfilePageService.checkCurrentColorSchemeByRadioButton();
        if (colorSchema == ColorSchema.LIGHT){
            userProfilePageService.pickDarkSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
            userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
            Assert.assertNotNull(userProfilePage.getSuccessAlert());
            userProfilePage.reload();
            userProfilePage.waitUntilPageIsLoaded(3);
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePageService.pickLightSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
        } else if (colorSchema == ColorSchema.DARK){
            userProfilePageService.pickLightSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
            userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
            Assert.assertNotNull(userProfilePage.getSuccessAlert());
            userProfilePage.reload();
            userProfilePage.waitUntilPageIsLoaded(3);
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePageService.pickDarkSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
        }
    }

    @Test
    public void changePasswordTest() {
        String tempPwd = "qqqqqqqqqqq";
        String shortPwd = "qqq";

        //Check 2 empty inputs
        Assert.assertTrue(userProfilePage.getPasswordInput().getAttribute("value").isEmpty(), "Password input is not empty");
        Assert.assertTrue(userProfilePage.getConfirmPasswordInput().getAttribute("value").isEmpty(), "Confirm Password input is not empty");
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getChangePasswordButtonDisabled(), 0), "Change Password button is not disabled");

        //Check 1 epmty input
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        Assert.assertFalse(userProfilePage.getPasswordInput().getAttribute("value").isEmpty(), "Password input is empty");
        Assert.assertTrue(userProfilePage.getConfirmPasswordInput().getAttribute("value").isEmpty(), "Confirm Password input is not empty");
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getChangePasswordButtonDisabled(), 0), "Change Password button is not disabled");

        //Check different values in 2 inputs
        userProfilePage.getConfirmPasswordInput().sendKeys("wwwwwwwwww");
        Assert.assertFalse(userProfilePage.getPasswordInput().getAttribute("value").isEmpty(), "Password input is empty");
        Assert.assertFalse(userProfilePage.getConfirmPasswordInput().getAttribute("value").isEmpty(), "Confirm Password input is empty");
        Assert.assertFalse(userProfilePage.isElementPresent(userProfilePage.getChangePasswordButtonDisabled(), 1), "Change Password button is not disabled");
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getChangePasswordButtonEnabled(), 1), "Change Password button is disabled");
        userProfilePage.getChangePasswordButtonEnabled().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getErrorAlert(), 1), "Error Alert is not present");

        //Check 1st input with value < 5 characters
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(shortPwd);
        userProfilePage.getChangePasswordButtonEnabled().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));

        //Check 2 inputs with value < 5 characters
        userProfilePage.getConfirmPasswordInput().clear();
        userProfilePage.getConfirmPasswordInput().sendKeys(shortPwd);
        userProfilePage.getChangePasswordButtonEnabled().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));

        //Check 2nd input with value < 5 characters
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        userProfilePage.getChangePasswordButtonEnabled().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));

        //Check successful password change
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        userProfilePage.getConfirmPasswordInput().clear();
        userProfilePage.getConfirmPasswordInput().sendKeys(tempPwd);
        Assert.assertEquals(userProfilePage.getPasswordInput().getAttribute("value"), userProfilePage.getConfirmPasswordInput().getAttribute("value"), "Password Inputs contain different values");
        userProfilePage.getChangePasswordButtonEnabled().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getSuccessAlert(), 1), "Success Alert is not present");
        userProfilePage.getHeader().logOut();
        DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, tempPwd);
        Assert.assertTrue(dashboardPage.getUrl().contains("dashboards"), "Dashboard page hasn't been opened");

        //Replace test data with default
        userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
        userProfilePage.waitUntilPageIsLoaded(3);
        userProfilePage.getPasswordInput().sendKeys(ADMIN1_PASS);
        userProfilePage.getConfirmPasswordInput().sendKeys(ADMIN1_PASS);
        userProfilePage.getChangePasswordButtonEnabled().click();
        userProfilePage.getHeader().logOut();
        DashboardPage dashboardPage2 = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
        Assert.assertTrue(dashboardPage2.getUrl().contains("dashboards"), "Dashboard page hasn't been opened");
    }
}
