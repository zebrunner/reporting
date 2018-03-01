package com.qaprosoft.zafira.tests.gui.components.modals;

import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ChangePasswordModalWindow extends AbstractModalWindow
{

	@FindBy(name = "password")
	private WebElement passwordInput;

	@FindBy(name = "confirmPassword")
	private WebElement confirmPasswordInput;

	@FindBy(xpath = ".//button[contains(text(), 'Change')]")
	private WebElement changeButton;

	public ChangePasswordModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public void changePassword(String password)
	{
		passwordInput.sendKeys(password);
		confirmPasswordInput.sendKeys(password);
		changeButton.click();
		waitUntilModalIsNotPresent();
	}

	public WebElement getPasswordInput()
	{
		return passwordInput;
	}

	public WebElement getConfirmPasswordInput()
	{
		return confirmPasswordInput;
	}

	public WebElement getChangeButton()
	{
		return changeButton;
	}
}
