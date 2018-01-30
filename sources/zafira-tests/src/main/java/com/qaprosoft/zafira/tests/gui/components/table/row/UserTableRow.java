package com.qaprosoft.zafira.tests.gui.components.table.row;

import com.qaprosoft.zafira.tests.gui.components.menus.UserSettingMenu;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserTableRow extends AbstractRow
{
	@FindBy(xpath = ".//td[1]//img[not(contains(@class, 'ng-hide'))] | .//td[1]//i[not(contains(@class, 'ng-hide'))]")
	private WebElement userPhoto;

	@FindBy(xpath = "//md-tooltip")
	private WebElement userIdTooltip;

	@FindBy(xpath = ".//td[2]/b")
	private WebElement username;

	@FindBy(xpath = ".//td[3]")
	private WebElement email;

	@FindBy(xpath = ".//td[4]")
	private WebElement firstLastName;

	@FindBy(xpath = ".//td[5]//span[contains(@class, 'label')]")
	private WebElement statusLabel;

	@FindBy(xpath = ".//td[6]/span")
	private WebElement registrationDate;

	@FindBy(xpath = ".//td[6]//span[contains(@class, 'time')]")
	private WebElement lastLogin;

	@FindBy(xpath = ".//td[7]//button[contains(@class, 'md-icon-button ')]")
	private UserSettingMenu userSettingMenu;

	public UserTableRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getUserPhoto()
	{
		return userPhoto;
	}

	public String hoverOnUserPhoto()
	{
		hoverOnElement(userPhoto);
		waitUntilElementIsPresent(userIdTooltip, 1);
		return userIdTooltip.getText();
	}

	public WebElement getUserIdTooltip()
	{
		return userIdTooltip;
	}

	public WebElement getUsername()
	{
		return username;
	}

	public String getUsernameText()
	{
		return username.getText();
	}

	public WebElement getEmail()
	{
		return email;
	}

	public String getEmailText()
	{
		return email.getText();
	}

	public WebElement getFirstLastName()
	{
		return firstLastName;
	}

	public String getFirstLastNameText()
	{
		return firstLastName.getText();
	}

	public WebElement getStatusLabel()
	{
		return statusLabel;
	}

	public String getStatusLabelText()
	{
		return statusLabel.getText();
	}

	public WebElement getRegistrationDate()
	{
		return registrationDate;
	}

	public String getRegistrationDateText()
	{
		return registrationDate.getText();
	}

	public WebElement getLastLogin()
	{
		return lastLogin;
	}

	public String getLastLoginText()
	{
		return lastLogin.getText();
	}

	public UserSettingMenu getUserSettingMenu()
	{
		return userSettingMenu;
	}

	public UserSettingMenu clickUserSettingMenu()
	{
		userSettingMenu.getRootElement().click();
		return userSettingMenu;
	}
}
