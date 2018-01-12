package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.qaprosoft.zafira.tests.gui.pages.UserProfilePage.ColorSchema;

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

    @Test
    public void testColorSchemaChange() {
        ColorSchema colorSchema = userProfilePage.checkCurrentColorSchemeByRadioButton();
        if (colorSchema == ColorSchema.LIGHT){
            Assert.assertTrue(userProfilePage.lightSchemaStyleIsDisplayed());
            Assert.assertTrue(userProfilePage.lightSchemaStyleIsDisplayed());
            userProfilePage.pickDarkSchemaRadioButton();
            Assert.assertTrue(userProfilePage.darkSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePage.lightSchemaStyleIsDisplayed());
        } else if (colorSchema == ColorSchema.DARK){
            Assert.assertTrue(userProfilePage.darkSchemaStyleIsDisplayed());
            userProfilePage.pickLightSchemaRadioButton();
            Assert.assertTrue(userProfilePage.lightSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePage.darkSchemaStyleIsDisplayed());
        }
    }

}
