package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.components.*;
import com.qaprosoft.zafira.tests.gui.components.modals.UploadImageModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.*;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NavigationTest extends AbstractTest {

	private DashboardPage dashboardPage;

	@BeforeMethod
	public void setup() {
		LoginPageService loginPageService = new LoginPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		this.dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		this.dashboardPage.waitUntilPageIsLoaded(20);
	}

	@Test
	public void verifyNavbarFunctionalityTest() {
		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		Assert.assertTrue(testRunTabMenu.isElementPresent(10), "Test run menu not visible!");
		TestRunPage testRunPage = testRunTabMenu.clickShowRunsButton();
		Assert.assertTrue(testRunPage.isOpened(), "Test run page is not opened");

		DashboardTabMenu dashboardTabMenu = dashboardPage.getNavbar().hoverOnDashboardTab();
		Assert.assertTrue(dashboardTabMenu.isElementPresent(10), "Dashboard menu not visible!");
		DashboardPage dashboardPage = dashboardTabMenu.clickDashboardByName("General Test");
		Assert.assertTrue(dashboardPage.isOpened(), "Dashboard page is not opened");

		TestCasePage testCasePage = dashboardPage.getNavbar().clickTestCasesTab();
		Assert.assertTrue(testCasePage.isOpened(), "Test cases page not opened!");

		UserPage userPage = dashboardPage.getNavbar().clickUsersTab();
		Assert.assertTrue(userPage.isOpened(), "Test cases page not opened!");

		MonitorPage monitorPage = dashboardPage.getNavbar().clickMonitorsTab();
		Assert.assertTrue(monitorPage.isOpened(), "Test cases page not opened!");
	}

	@Test
	public void verifyHeaderFunctionalityTest() {

		ProjectFilterMenu projectFilterMenu = dashboardPage.getHeader().clickProjectFilterButton();
		Assert.assertTrue(projectFilterMenu.isElementPresent(projectFilterMenu.getClearButton(), 2), "Clear button is not present");
		Assert.assertTrue(projectFilterMenu.isElementPresent(projectFilterMenu.getCreateButton(), 2), "Create button is not present");
		Assert.assertTrue(projectFilterMenu.getProjectNames().contains("UNKNOWN"), "UNKNOWN project is not present");

		UserMenu userMenu = dashboardPage.getHeader().clickUserMenuButton();
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getUserProfileButton(), 2), "User profile button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getUserPerformanceButton(), 2), "User performance button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getIntegrationsButton(), 2), "Integrations button is not present");
		Assert.assertTrue(userMenu.isElementPresent(userMenu.getLogoutButton(), 2), "Logout button is not present");

		Assert.assertEquals(dashboardPage.getHeader().getZafiraLogo().getText(), "ZAFIRA", "Invalid zafira logo text in header");
		Assert.assertTrue(dashboardPage.getHeader().getCompanyLogoBackgroundIcon().isDisplayed() ||
				dashboardPage.getHeader().getCompanyLogo().isDisplayed(), "Invalid company icon in header");

		dashboardPage.clickOutside();

		dashboardPage.getNavbar().clickTestCasesTab();
		dashboardPage.waitUntilPageIsLoaded(10);

		dashboardPage.getHeader().getZafiraLogo().click();
		dashboardPage.waitUntilPageIsLoaded(10);

		Assert.assertTrue(dashboardPage.isOpened(), "Dashboards page not opened");

		dashboardPage.hoverOnElement(dashboardPage.getHeader().getCompanyLogoBackgroundIcon());
		Assert.assertTrue(dashboardPage.isElementPresent(dashboardPage.getHeader().getCompanyProfilePhotoHoverIcon(), 1),
				"Settings icon not present on company icon hover");

		UploadImageModalWindow uploadImageModalWindow = dashboardPage.getHeader().clickCompanyPhotoHoverIcon();
		dashboardPage.waitUntilPageIsLoaded(10);

		Assert.assertTrue(uploadImageModalWindow.isElementPresent(10), "Company photo modal window not opened");
		Assert.assertEquals(uploadImageModalWindow.getHeaderText(), "Profile image", "Incorrect modal window name");
		uploadImageModalWindow.closeModalWindow();

		driver.manage().window().setSize(new Dimension(700, 1000));

		Assert.assertTrue(dashboardPage.isElementPresent(dashboardPage.getHeader().getMobileMenuButton(), 2), "Mobile nav bar button is not present");
		dashboardPage.getHeader().getMobileMenuButton().click();

		driver.manage().window().maximize();

		UserProfilePage userProfilePage = dashboardPage.getHeader().goToUserProfilePage();
		Assert.assertTrue(userProfilePage.isOpened(), "User profile page not opened");

		IntegrationsPage integrationsPage = dashboardPage.getHeader().goToIntegrationsPage();
		Assert.assertTrue(integrationsPage.isOpened(), "Integrations page not opened");

		UserPerformancePage userPerformancePage = dashboardPage.getHeader().goToUserPerformancePage();
		Assert.assertTrue(userPerformancePage.isOpened(), "User performance page not opened");

		dashboardPage.getHeader().clickUserMenuButton();
		LoginPage loginPage = dashboardPage.getHeader().logOut();
		Assert.assertTrue(loginPage.isOpened(), "Login page not opened");
	}
}
