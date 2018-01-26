package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import com.qaprosoft.zafira.tests.gui.pages.MonitorPage;
import com.qaprosoft.zafira.tests.gui.pages.TestCasePage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Navbar extends AbstractUIObject
{

	private static final String CONTAINER_LOCATOR = "nav-container";

	@FindBy(id = CONTAINER_LOCATOR)
	private WebElement container;

	@FindBy(xpath = ".//ul[preceding-sibling::a[.//*[text() = 'Dashboards']]]")
	private DashboardTabMenu dashboardTabMenu;

	@FindBy(xpath = ".//ul[preceding-sibling::a[.//*[text() = 'Test runs']")
	private TestRunTabMenu testRunTabMenu;

	@FindBy(xpath = ".//*[text() = 'Dashboards']]")
	private WebElement dashboardsTab;

	@FindBy(xpath = ".//*[text() = 'Test runs']]")
	private WebElement testRunsTab;

	@FindBy(xpath = ".//*[text() = 'Test cases']]")
	private WebElement testCasesTab;

	@FindBy(xpath = ".//*[text() = 'Users']]")
	private WebElement usersTab;

	@FindBy(xpath = ".//*[text() = 'Monitors']]")
	private WebElement monitorsTab;

	public Navbar(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public DashboardTabMenu hoverOnDashboardTab()
	{
		super.hoverOnElement(this.dashboardsTab);
		super.waitUntilElementIsPresent(getRootElement(), 10);
		return this.dashboardTabMenu;
	}

	public TestRunTabMenu hoverOnTestRunTab()
	{
		super.hoverOnElement(this.testRunsTab);
		super.waitUntilElementIsPresent(this.testRunTabMenu.getRootElement(), 10);
		return this.testRunTabMenu;
	}

	public TestRunPage goToTestRunPage()
	{
		TestRunTabMenu testRunTabMenu = hoverOnTestRunTab();
		return testRunTabMenu.clickShowRunsButton();
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
}
