package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import com.qaprosoft.zafira.tests.services.gui.DashboardPageService;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DashboardPageTest extends AbstractTest {

    private DashboardPage dashboardPage;
    private DashboardPageService dashboardPageService;

    @BeforeMethod
    public void loginUser()
    {
        LoginPageService loginPageService = new LoginPageService(driver);
        dashboardPageService = new DashboardPageService(driver);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS, Integer.parseInt(GENERAL_DASHBOARD_ID));
    }

    @Test
    public void test(){

    }

}
