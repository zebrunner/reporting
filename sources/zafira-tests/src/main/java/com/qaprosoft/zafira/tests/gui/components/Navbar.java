package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Navbar extends AbstractPage implements IElement
{

	private static final String CONTAINER_LOCATOR = "nav-container";

	@FindBy(id = CONTAINER_LOCATOR)
	private WebElement container;

	@FindBy(xpath = "//*[@id  ='nav']//a[.//*[text()='Dashboards']]")
	private WebElement dashboardsTab;

	private DashboardTabMenu dashboardTabMenu;

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
		this.dashboardTabMenu = new DashboardTabMenu(driver, path);
	}

	public DashboardTabMenu hoverOnDashboardTab()
	{
		super.hoverOnElement(this.dashboardsTab);
		super.waitUntilElementIsNotPresent(this.dashboardTabMenu.getElement(), 10);
		return this.dashboardTabMenu;
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

	@Override
	public By getLocator()
	{
		return By.id(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
