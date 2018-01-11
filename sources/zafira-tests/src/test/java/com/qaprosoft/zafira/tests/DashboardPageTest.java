package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DashboardPageTest extends AbstractTest {

    private static DashboardPage dashboardPage;

    @BeforeMethod
    public void loginUser()
    {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        dashboardPage = loginPage.login(ADMIN1_USER, ADMIN1_PASS);
    }

    @Test
    public void testOpenUserProfile(){
        UserProfilePage userProfilePage = dashboardPage.goToUserProfilePage();
        Assert.assertTrue(userProfilePage.isOpened(), "User Profile is not opened!");
    }

}
