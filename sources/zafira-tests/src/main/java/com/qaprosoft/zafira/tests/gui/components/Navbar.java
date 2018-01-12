package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import com.qaprosoft.zafira.tests.gui.pages.MonitorPage;
import com.qaprosoft.zafira.tests.gui.pages.TestCasePage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Navbar extends AbstractPage implements IElement
{

	private static final String CONTAINER_LOCATOR = "nav-container";

	@FindBy(id = CONTAINER_LOCATOR)
	private WebElement container;

	private DashboardTabMenu dashboardTabMenu;

	private TestRunTabMenu testRunTabMenu;

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
		this.dashboardTabMenu = new DashboardTabMenu(driver, path);
		this.testRunTabMenu = new TestRunTabMenu(driver, path);
	}

	public DashboardTabMenu hoverOnDashboardTab()
	{
		super.hoverOnElement(this.dashboardsTab);
		super.waitUntilElementIsPresent(this.dashboardTabMenu.getElement(), 10);
		return this.dashboardTabMenu;
	}

	public TestRunTabMenu hoverOnTestRunTab()
	{
		super.hoverOnElement(this.testRunsTab);
		super.waitUntilElementIsPresent(this.testRunTabMenu.getElement(), 10);
		return this.testRunTabMenu;
	}

	public TestCasePage clickTestCasesTab()
	{
		this.testCasesTab.click();
		return new TestCasePage(driver);
	}

	public UserPage clickUsersTab()
	{
		this.usersTab.click();
		return new UserPage(driver);
	}

	public MonitorPage clickMonitorsTab()
	{
		this.monitorsTab.click();
		return new MonitorPage(driver);
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
