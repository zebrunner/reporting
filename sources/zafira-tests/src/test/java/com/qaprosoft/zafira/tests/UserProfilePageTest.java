package com.qaprosoft.zafira.tests;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.tests.exceptions.NoDashboardsWereLoadedException;
import com.qaprosoft.zafira.tests.gui.components.modals.UploadImageModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage.ColorSchema;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.UserPageService;
import com.qaprosoft.zafira.tests.services.gui.UserProfilePageService;

public class UserProfilePageTest extends AbstractTest {

    private UserProfilePage userProfilePage;
    private UserProfilePageService userProfilePageService;
    private LoginPageService loginPageService;
    private JavascriptExecutor jse;

    @BeforeMethod
    public void loginUser()
    {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPageService = new LoginPageService(driver);
        userProfilePageService = new UserProfilePageService(driver);
        jse = (JavascriptExecutor) driver;
        DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
        dashboardPage.waitUntilPageIsLoaded();
        userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
        userProfilePage.waitUntilPageIsLoaded();
    }

    @Test(groups = {"acceptance", "profile"})
    public void generateTokenTest() {
        // Generate token
        userProfilePageService.generateToken();
        userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
        Assert.assertNotNull(userProfilePage.getSuccessAlert());
        Assert.assertFalse(userProfilePage.getWebElementValue(userProfilePage.getTokenInput()).isEmpty());
        userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);

        //Copy token
        userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);
        Assert.assertTrue(userProfilePageService.copyToken());
    }

    @Test(groups = {"acceptance", "profile", "user"})
    public void changeUserProfilePhotoTest(){
        userProfilePage.hoverOnElement(userProfilePage.getLoadProfilePhotoIcon());
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getLoadProfilePhotoHoverIcon(), 1),
                "Settings icon not present on user profile icon hover");

        UploadImageModalWindow uploadImageModalWindow = userProfilePageService.goToUploadImageModalWindow();
        Assert.assertTrue(uploadImageModalWindow.isElementPresent(10), "Company photo modal window not opened");
        Assert.assertEquals(uploadImageModalWindow.getHeaderText(), "Profile image", "Incorrect modal window name");
        uploadImageModalWindow.closeModalWindow();
    }

    @Test(groups = {"acceptance", "profile", "user"})
    public void changeColorSchemaTest() {
        ColorSchema colorSchema = userProfilePageService.checkCurrentColorSchemeByRadioButton();
        if (colorSchema == ColorSchema.LIGHT){

            //Check if schema changes on UI
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePageService.pickDarkSchemaRadioButton();
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());

            //Check if schema is saved on backend

            userProfilePage.waitUntilLoadingContainerDisappears(5);
            userProfilePage.waitUntilPageIsLoaded(2);
            userProfilePageService.pickDarkSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
            userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
            Assert.assertNotNull(userProfilePage.getSuccessAlert());
            userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);
            userProfilePage.reload();
            userProfilePage.waitUntilLoadingContainerDisappears(5);
            userProfilePage.waitUntilPageIsLoaded(2);
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePageService.pickLightSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();

        } else if (colorSchema == ColorSchema.DARK){

            //Check if schema changes on UI
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());
            userProfilePageService.pickLightSchemaRadioButton();
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePage.reload();
            Assert.assertTrue(userProfilePageService.darkSchemaStyleIsDisplayed());

            //Check if schema is saved on backend
            userProfilePage.waitUntilLoadingContainerDisappears(5);
            userProfilePage.waitUntilPageIsLoaded(2);
            userProfilePageService.pickLightSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
            userProfilePage.waitUntilElementIsPresent(userProfilePage.getSuccessAlert(),1);
            Assert.assertNotNull(userProfilePage.getSuccessAlert());
            userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);
            userProfilePage.reload();
            userProfilePage.waitUntilLoadingContainerDisappears(5);
            userProfilePage.waitUntilPageIsLoaded(2);
            Assert.assertTrue(userProfilePageService.lightSchemaStyleIsDisplayed());
            userProfilePageService.pickDarkSchemaRadioButton();
            userProfilePage.getSavePreferencesButton().click();
        }
    }

    @Test(groups = {"acceptance", "profile", "user"})
    public void changePreferencesTest(){

        WebElement chosenDashboard = null;
        WebElement generalDashboard = null;
        WebElement nightlyDashboard = null;
        String chosenDashboardName = null;
        String testChosenDashboardName;
        jse.executeScript("arguments[0].scrollIntoView(true);", userProfilePage.getSaveUserProfileButton());
        userProfilePage.getDefaultDashboardSelect().click();
        userProfilePage.waitUntilElementToBeClickableByBackdropMask(userProfilePage.getDashboardSelectValues().get(0), 1);
        List<WebElement> webElements = userProfilePage.getDashboardSelectValues();
        if (CollectionUtils.isEmpty(webElements)){
            throw new NoDashboardsWereLoadedException("No dashboards are present in dropdown");
        }
        for(WebElement we: webElements){
            if (userProfilePage.hasSelectedAttribute(we)){
                chosenDashboard = we;
                chosenDashboardName = we.getText();
                if(we.getText().contains("General")){
                    generalDashboard = chosenDashboard;
                }
            } else if (we.getText().contains("General")){
                generalDashboard = we;
            } else if (we.getText().contains("Nightly")){
                nightlyDashboard = we;
            }
        }
        if (chosenDashboard != null && nightlyDashboard != null && generalDashboard != null){
            if (chosenDashboard.equals(generalDashboard)){
                nightlyDashboard.click();
            } else {
                generalDashboard.click();
            }
        }
        testChosenDashboardName = userProfilePage.getDefaultDashboardSelect().getText();
        userProfilePage.getSavePreferencesButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getSuccessAlert(), 2), "Preferences saving is not successful");
        userProfilePage.reload();
        userProfilePage.waitUntilLoadingContainerDisappears(5);
        Assert.assertEquals((userProfilePage.getDefaultDashboardSelect().getText()), testChosenDashboardName, "Saved dashboard mismatch with previously chosen");
        userProfilePage.getDefaultDashboardSelect().click();
        userProfilePage.waitUntilElementToBeClickableByBackdropMask(userProfilePage.getDashboardSelectValues().get(0), 1);
        List<WebElement> webElements2 = userProfilePage.getDashboardSelectValues();
        for(WebElement we: webElements2){
            if(we.getText().equals(chosenDashboardName)){
                we.click();
            }
        }
        userProfilePage.getSavePreferencesButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getSuccessAlert(), 2), "Preferences saving is not successful");
    }

    @Test(groups = {"acceptance", "profile", "user"})
    public void changePasswordTest() {

        String tempPwd = "qqqqqqqqqqq";
//        String shortPwd = "qqq";

        String email = String.format("test+%s@test.test", RandomUtils.nextInt(0, 10000));
        String username = "username" + RandomStringUtils.randomAlphabetic(15);
        String password = "Password1";
        UserPage userPage = userProfilePage.getNavbar().clickUsersTab();
        UserPageService userPageService = new UserPageService(driver);
        userPage.waitUntilPageIsLoaded();
        userPageService.createUser(email, username, password);
        loginPageService.logout();
        DashboardPage dashboardPage = loginPageService.login(username, password);
        userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
        userProfilePage.waitUntilPageIsLoaded();

        //Check 2 empty inputs
        Assert.assertTrue(userProfilePage.getWebElementValue(userProfilePage.getPasswordInput()).isEmpty(), "Password input is not empty");
        Assert.assertTrue(userProfilePage.getWebElementValue(userProfilePage.getOldPassword()).isEmpty(), "Confirm Password input is not empty");
        Assert.assertTrue(userProfilePage.hasDisabledAttribute(userProfilePage.getChangePasswordButton()), "Change Password button is not disabled");

        //Check 1 epmty input
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        Assert.assertFalse(userProfilePage.getWebElementValue(userProfilePage.getPasswordInput()).isEmpty(), "Password input is empty");
        Assert.assertTrue(userProfilePage.getWebElementValue(userProfilePage.getOldPassword()).isEmpty(), "Confirm Password input is not empty");
        Assert.assertTrue(userProfilePage.hasDisabledAttribute(userProfilePage.getChangePasswordButton()), "Change Password button is not disabled");

        //Check different values in 2 inputs
        userProfilePage.getOldPassword().sendKeys("wwwwwwwwww");
        Assert.assertFalse(userProfilePage.getWebElementValue(userProfilePage.getPasswordInput()).isEmpty(), "Password input is empty");
        Assert.assertFalse(userProfilePage.getWebElementValue(userProfilePage.getOldPassword()).isEmpty(), "Confirm Password input is empty");
        Assert.assertFalse(userProfilePage.hasDisabledAttribute(userProfilePage.getChangePasswordButton()), "Change Password button is not disabled");
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getChangePasswordButton(), 1), "Change Password button is disabled");
        userProfilePage.getChangePasswordButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getErrorAlert(), 1), "Error Alert is not present");

        /*//Check 1st input with value < 5 characters
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(shortPwd);
        userProfilePage.getChangePasswordButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));

        //Check 2 inputs with value < 5 characters
        userProfilePage.getOldPassword().clear();
        userProfilePage.getOldPassword().sendKeys(shortPwd);
        userProfilePage.getChangePasswordButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));

        //Check 2nd input with value < 5 characters
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        userProfilePage.getChangePasswordButton().click();
        Assert.assertTrue(userProfilePage.isElementPresent(userProfilePage.getWarningAlert(), 1));*/

        //Check successful password change
        userProfilePage.getPasswordInput().clear();
        userProfilePage.getPasswordInput().sendKeys(tempPwd);
        userProfilePage.getOldPassword().clear();
        userProfilePage.getOldPassword().sendKeys(password);
        //Assert.assertEquals(userProfilePage.getWebElementValue(userProfilePage.getPasswordInput()), userProfilePage.getWebElementValue(userProfilePage.getOldPassword()), "Password Inputs contain different values");
        userProfilePage.getChangePasswordButton().click();
        userProfilePage.waitUntilElementWithTextIsPresent(userProfilePage.getSuccessAlert(), "Password changed", 5);
        userPage.waitUntilAlertWithTextIsPresent(5);
        Assert.assertEquals(userProfilePage.getSuccessAlert().getText(), "Password changed", "Success Alert is incorrect");
        userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);
        userProfilePage.getHeader().logOut();
        dashboardPage = loginPageService.login(username, tempPwd);
        Assert.assertTrue(dashboardPage.getUrl().contains("dashboards"), "Dashboard page hasn't been opened");

        //Replace test data with default
        userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
        userProfilePage.waitUntilPageIsLoaded(2);
        userProfilePage.getPasswordInput().sendKeys(password);
        userProfilePage.getOldPassword().sendKeys(tempPwd);
        userProfilePage.getChangePasswordButton().click();
        userProfilePage.getHeader().logOut();
        DashboardPage dashboardPage2 = loginPageService.login(username, password);
        Assert.assertTrue(dashboardPage2.getUrl().contains("dashboards"), "Dashboard page hasn't been opened");
    }

    @Test(groups = {"acceptance", "profile", "user"})
    public void changeUserProfileInfoTest (){

        String firstName = userProfilePage.getWebElementValue(userProfilePage.getFirstNameInput());
        String lastName = userProfilePage.getWebElementValue(userProfilePage.getLastNameInput());
//        String email = userProfilePage.getWebElementValue(userProfilePage.getEmailInput());

        //Check if username is disabled
        Assert.assertTrue(userProfilePage.isElementPresent
                (userProfilePage.getUserNameInput(),1), "Username input is enabled or is not visible!");

        //Check disabled save for all empty fields
        userProfilePage.getFirstNameInput().clear();
        userProfilePage.getLastNameInput().clear();
        //userProfilePage.getEmailInput().clear();
        //Assert.assertTrue(userProfilePage.hasDisabledAttribute(userProfilePage.getSaveUserProfileButton()),"User profile button is enabled with empty fields");

        //Check disabled empty first&last name incorrect email
        //userProfilePage.getEmailInput().sendKeys("email");
        //Assert.assertTrue(userProfilePage.hasDisabledAttribute(userProfilePage.getSaveUserProfileButton()), "User profile button is enabled with incorrect email");

        //Check enabled empty first&last name correct email
        //email = "text@test.com";
        //userProfilePage.getEmailInput().clear();
        //userProfilePage.getEmailInput().sendKeys(email);
        //Assert.assertFalse(userProfilePage.hasDisabledAttribute(userProfilePage.getSaveUserProfileButton()), "User profile button is disabled");

        //Check save empty first&last name
        userProfilePage.getSaveUserProfileButton().click();
        //userProfilePage.waitUntilElementWithTextIsPresent(userProfilePage.getSuccessAlert(), "User profile updated", 5);
        userProfilePage.waitUntilAlertWithTextIsPresent(15);
        Assert.assertEquals(userProfilePage.getSuccessAlert().getText(), "User profile updated", "Save User profile action is not successful");
        userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);

        //Check enabled for all filled out data
        firstName = "ftest";
        lastName = "ltest";
        userProfilePage.getFirstNameInput().sendKeys(firstName);
        userProfilePage.getLastNameInput().sendKeys(lastName);
        Assert.assertFalse(userProfilePage.hasDisabledAttribute(userProfilePage.getSaveUserProfileButton()), "User profile button is disabled");

        //Check save for all filled out data
        userProfilePage.getSaveUserProfileButton().click();
        //userProfilePage.waitUntilElementWithTextIsPresent(userProfilePage.getSuccessAlert(), "User profile updated", 5);
        userProfilePage.waitUntilAlertWithTextIsPresent(15);
        Assert.assertEquals(userProfilePage.getSuccessAlert().getText(), "User profile updated", "Save User profile action is not successful");
        userProfilePage.waitUntilElementIsNotPresent(userProfilePage.getSuccessAlert(),2);
    }
}
