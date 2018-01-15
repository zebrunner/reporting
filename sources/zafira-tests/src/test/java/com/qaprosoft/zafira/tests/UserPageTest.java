package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserPageTest extends AbstractTest
{

	private UserPage userPage;

	@BeforeMethod
	public void setup()
	{
		LoginPageService loginPageService = new LoginPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService
				.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		this.userPage = dashboardPage.getNavbar().clickUsersTab();
		this.userPage.waitUntilPageIsLoaded(10);
	}

	@Test
	public void verifyUserPageTest()
	{
	}
}
