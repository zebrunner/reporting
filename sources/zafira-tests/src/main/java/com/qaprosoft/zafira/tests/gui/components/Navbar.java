package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Navbar extends AbstractPage
{

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Dashboards']]")
	private WebElement dashboardsTab;

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Test runs']]")
	private WebElement testRunsTab;

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Test cases']]")
	private WebElement testCasesTab;

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Users']]")
	private WebElement usersTab;

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Monitors']]")
	private WebElement monitorsTab;

	public Navbar(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public WebElement getDashboardsTab()
	{
		return dashboardsTab;
	}

	public WebElement getTestRunsTab()
	{
		return testRunsTab;
	}

	public WebElement getTestCasesTab()
	{
		return testCasesTab;
	}

	public WebElement getUsersTab()
	{
		return usersTab;
	}

	public WebElement getMonitorsTab()
	{
		return monitorsTab;
	}
}
