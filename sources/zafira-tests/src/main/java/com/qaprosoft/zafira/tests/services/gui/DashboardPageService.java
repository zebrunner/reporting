package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.openqa.selenium.WebDriver;

public class DashboardPageService extends AbstractPageService
{

	private DashboardPage dashboardPage;

	public DashboardPageService(WebDriver driver)
	{
		super(driver);
		this.dashboardPage = new DashboardPage(driver);
	}

	public UserProfilePage goToUserProfilePage()
	{
		dashboardPage.waitUntilElementIsPresent(dashboardPage.getHeader().getUserMenuButton(),2);
		dashboardPage.getHeader().clickUserMenuButton().getUserProfileButton().click();
		return new UserProfilePage(driver);
	}
}
