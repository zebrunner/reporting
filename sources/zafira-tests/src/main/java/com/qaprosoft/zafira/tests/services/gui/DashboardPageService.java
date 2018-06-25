package com.qaprosoft.zafira.tests.services.gui;

import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;

public class DashboardPageService extends AbstractPageService
{

	private DashboardPage dashboardPage;

	public DashboardPageService(WebDriver driver)
	{
		super(driver);
		this.dashboardPage = new DashboardPage(driver);
	}
}
