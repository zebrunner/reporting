package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserSettingMenu extends AbstractUIObject
{

	private static final String CONTAINER_LOCATOR = "//div[preceding-sibling::header]/md-menu-content";

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[./*[text() = 'build']]")
	private WebElement editProfileButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[./*[text() = 'lock_outline']]")
	private WebElement changePasswordButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//a[./*[text() = 'timeline']]")
	private WebElement performanceButton;

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	public UserSettingMenu(WebDriver driver)
	{
		super(driver, null);
	}

	public WebElement getEditProfileButton()
	{
		return editProfileButton;
	}

	public WebElement getChangePasswordButton()
	{
		return changePasswordButton;
	}

	public WebElement getPerformanceButton()
	{
		return performanceButton;
	}

	public CreateUserModalWindow clickEditProfileButton()
	{
		editProfileButton.click();
		return new CreateUserModalWindow(driver);
	}

	public ChangePasswordModalWindow clickChangePasswordButton()
	{
		changePasswordButton.click();
		return new ChangePasswordModalWindow(driver);
	}

	public DashboardPage clickPerformanceButton()
	{
		performanceButton.click();
		DashboardPage dashboardPage = new DashboardPage(driver);
		dashboardPage.waitUntilPageIsLoaded(10);
		return dashboardPage;
	}

	@Override
	public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
