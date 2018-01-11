package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.components.DashboardTabMenu;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NavbarTest extends AbstractTest
{

	private DashboardPage dashboardPage;

	@BeforeMethod
	public void setup()
	{
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		this.dashboardPage = loginPage.login(ADMIN1_USER, ADMIN1_PASS);
		this.dashboardPage.waitUntilPageIsLoaded(20);
	}

	@Test
	public void verifyNavbarFunctionalityTest()
	{
		DashboardTabMenu dashboardTabMenu = dashboardPage.getNavbar().hoverOnDashboardTab();
		DashboardPage dashboardPage = dashboardTabMenu.clickDashboardByName("General Test");
	}
}
