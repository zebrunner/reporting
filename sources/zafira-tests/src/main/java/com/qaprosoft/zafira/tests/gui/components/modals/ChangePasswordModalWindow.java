package com.qaprosoft.zafira.tests.gui.components.modals;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ChangePasswordModalWindow extends AbstractModalWindow
{

	@FindBy(name = "password")
	private WebElement passwordInput;

	@FindBy(xpath = ".//button[contains(text(), 'Apply')]")
	private WebElement changeButton;

	public ChangePasswordModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public void changePassword(String password)
	{
		passwordInput.sendKeys(password);
		changeButton.click();
		waitUntilModalIsNotPresent();
	}

	public WebElement getPasswordInput()
	{
		return passwordInput;
	}

	public WebElement getChangeButton()
	{
		return changeButton;
	}
}
