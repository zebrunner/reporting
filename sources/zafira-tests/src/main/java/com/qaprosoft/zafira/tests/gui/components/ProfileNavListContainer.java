package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfileNavListContainer extends AbstractPage
{

	@FindBy(xpath = "//header//button//img")
	private WebElement profilePhoto;

	@FindBy(xpath = "//header//button[.//img]/small")
	private WebElement profileUsername;

	@FindBy(xpath = "//a[./*[text() = 'My profile']]")
	private WebElement myProfileButton;

	@FindBy(xpath = "//a[./*[text() = 'Performance']]")
	private WebElement performanceButton;

	@FindBy(xpath = "//a[./*[text() = 'Integrations']]")
	private WebElement integrationsButton;

	@FindBy(xpath = "//a[./*[text() = 'Logout']]")
	private WebElement logoutButton;

	public ProfileNavListContainer(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public WebElement getProfilePhoto()
	{
		return profilePhoto;
	}

	public WebElement getProfileUsername()
	{
		return profileUsername;
	}
}
