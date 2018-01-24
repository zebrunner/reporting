package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserSearchBlock extends AbstractSearchBlock
{

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@name = 'id']")
	private WebElement idInput;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@name = 'username']")
	private WebElement usernameInput;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@name = 'email']")
	private WebElement emailInput;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@name = 'firstLastName']")
	private WebElement firstLastNameInput;

	@FindBy(xpath = CONTAINER_LOCATOR + "//button[.//i[text() = 'today']]")
	private WebElement calendarButton;

	public UserSearchBlock(WebDriver driver)
	{
		super(driver);
	}

	public void typeId(String value)
	{
		this.idInput.sendKeys(value);
	}

	public void typeUsername(String value)
	{
		this.usernameInput.sendKeys(value);
	}

	public void typeEmail(String value)
	{
		this.emailInput.sendKeys(value);
	}

	public void typeFirstLastName(String value)
	{
		this.firstLastNameInput.sendKeys(value);
	}

	public void clickCalendarButton()
	{
		this.calendarButton.click();
	}

	public String getIdInputValue()
	{
		return getWebElementValue(idInput);
	}

	public String getUsernameValue()
	{
		return getWebElementValue(usernameInput);
	}

	public String getEmailValue()
	{
		return getWebElementValue(emailInput);
	}

	public String getFirstLastNameValue()
	{
		return getWebElementValue(firstLastNameInput);
	}

	public WebElement getIdInput()
	{
		return idInput;
	}

	public WebElement getUsernameInput()
	{
		return usernameInput;
	}

	public WebElement getEmailInput()
	{
		return emailInput;
	}

	public WebElement getFirstLastNameInput()
	{
		return firstLastNameInput;
	}

	public WebElement getCalendarButton()
	{
		return calendarButton;
	}
}
