package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.components.DashboardTabMenu;
import com.qaprosoft.zafira.tests.gui.components.ProjectFilter;
import com.qaprosoft.zafira.tests.gui.components.TestRunTabMenu;
import com.qaprosoft.zafira.tests.gui.components.UserMenu;
import com.qaprosoft.zafira.tests.gui.pages.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NavigationTest extends AbstractTest
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
		Assert.assertTrue(dashboardTabMenu.isElementPresent(10), "Dashboard menu not visible!");

		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		Assert.assertTrue(testRunTabMenu.isElementPresent(10), "Test run menu not visible!");

		TestCasePage testCasePage = dashboardPage.getNavbar().clickTestCasesTab();
		Assert.assertTrue(testCasePage.isOpened(), "Test cases page not opened!");

		UserPage userPage = dashboardPage.getNavbar().clickUsersTab();
		Assert.assertTrue(userPage.isOpened(), "Test cases page not opened!");

		MonitorPage monitorPage = dashboardPage.getNavbar().clickMonitorsTab();
		Assert.assertTrue(monitorPage.isOpened(), "Test cases page not opened!");
	}

	@Test
	public void verifyHeaderFunctionalityTest()
	{
		ProjectFilter projectFilter = this.dashboardPage.getHeader().clickProjectFilterButton();
		Assert.assertTrue(projectFilter.isElementPresent(projectFilter.getClearButton(), 2), "Clear button is not present");
		Assert.assertTrue(projectFilter.isElementPresent(projectFilter.getCreateButton(),  2), "Create button is not present");
		Assert.assertTrue(projectFilter.getProjectNames().contains("UNKNOWN"), "UNKNOWN project is not present");

		UserMenu userMenu = this.dashboardPage.getHeader().clickUserMenuButton();
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getUserProfileButton(),  2), "User profile button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getUserPerformanceButton(),  2), "User performance button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getIntegrationsButton(),  2), "Integrations button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getLogoutButton(),  2), "Logout button is not present");

		Assert.assertEquals(this.dashboardPage.getHeader().getZafiraLogo().getText(), "ZAFIRA", "Invalid zafira logo text in header");
		Assert.assertTrue(this.dashboardPage.getHeader().getCompanyLogoBackgroundIcon().isDisplayed(), "Invalid company icon in header");

		this.dashboardPage.clickOutside();

		this.dashboardPage.getNavbar().clickTestCasesTab();
		this.dashboardPage.waitUntilPageIsLoaded(10);

		this.dashboardPage.getHeader().getZafiraLogo().click();
		this.dashboardPage.waitUntilPageIsLoaded(10);

		Assert.assertTrue(this.dashboardPage.isOpened(), "Dashboards page not opened");

		this.dashboardPage.hoverOnElement(this.dashboardPage.getHeader().getCompanyLogoBackgroundIcon());
		Assert.assertTrue(this.dashboardPage.isElementPresent(this.dashboardPage.getHeader().getCompanyProfilePhotoHoverIcon(), 1),
				"Settings icon not present on company icon hover");
	}
}
