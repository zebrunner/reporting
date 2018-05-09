package com.qaprosoft.zafira.tests.gui.components.menus;

import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserSettingMenu extends AbstractMenu
{

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[./*[text() = 'build']]")
	private WebElement editProfileButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[./*[text() = 'lock_outline']]")
	private WebElement changePasswordButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//a[./*[text() = 'timeline']]")
	private WebElement performanceButton;

	public UserSettingMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
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
		waitUntilElementToBeClickableWithBackdropMask(editProfileButton, 2);
		editProfileButton.click();
		return new UserPage(driver).getCreateUserModalWindow();
	}

	public ChangePasswordModalWindow clickChangePasswordButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(changePasswordButton, 2);
		changePasswordButton.click();
		return new UserPage(driver).getChangePasswordModalWindow();
	}

	public DashboardPage clickPerformanceButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(performanceButton, 2);
		performanceButton.click();
		DashboardPage dashboardPage = new DashboardPage(driver);
		dashboardPage.waitUntilPageIsLoaded();
		return dashboardPage;
	}
}
