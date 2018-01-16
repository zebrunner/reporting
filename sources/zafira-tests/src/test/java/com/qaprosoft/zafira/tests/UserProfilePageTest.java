package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import com.qaprosoft.zafira.tests.services.gui.DashboardPageService;
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

    @FindBy(xpath="//button[@type='submit']")
    private WebElement loginButton;

    @BeforeMethod
    public void loginUser()
    {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        LoginPageService loginPageService = new LoginPageService(driver);
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
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
        } else if (colorSchema == ColorSchema.DARK){
            userProfilePageService.pickLightSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
            userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
            Assert.assertNotNull(userProfilePage.getSuccessAlert());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
        }
    }

}
