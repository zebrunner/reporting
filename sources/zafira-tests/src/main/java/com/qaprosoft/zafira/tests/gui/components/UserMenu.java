package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserMenu extends AbstractPage
{

	@FindBy(xpath = "//header//button//img")
	private WebElement userProfilePhoto;

	@FindBy(xpath = "//header//button[.//img]/small")
	private WebElement userProfileName;

	@FindBy(xpath = "//md-menu-item[descendant::a[contains(@href,'profile')]]")
	private WebElement userProfileButton;

	@FindBy(xpath = "//a[./*[text() = 'Performance']]")
	private WebElement userPerformanceButton;

	@FindBy(xpath = "//a[./*[text() = 'Integrations']]")
	private WebElement integrationsButton;

	@FindBy(xpath = "//a[./*[text() = 'Logout']]")
	private WebElement logoutButton;

	public UserMenu(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public WebElement getUserProfilePhoto()
	{
		return userProfilePhoto;
	}

	public WebElement getUserProfileName()
	{
		return userProfileName;
	}

	public WebElement getUserProfileButton() {
		super.waitUntilElementIsPresent(userProfileButton,2);
		return userProfileButton;
	}

	public WebElement getUserPerformanceButton() {
		super.waitUntilElementIsPresent(userPerformanceButton,2);
		return userPerformanceButton;
	}

	public WebElement getIntegrationsButton() {
		super.waitUntilElementIsPresent(integrationsButton,2);
		return integrationsButton;
	}

	public WebElement getLogoutButton() {
		super.waitUntilElementIsPresent(logoutButton,2);
		return logoutButton;
	}
}
