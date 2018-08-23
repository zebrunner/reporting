package com.qaprosoft.zafira.tests.gui.components.modals;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.models.dto.application.user.UserType;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;

public class CreateUserModalWindow extends AbstractModalWindow
{

	@FindBy(name = "firstName")
	private WebElement firstNameInput;

	@FindBy(name = "lastName")
	private WebElement lastNameInput;

	@FindBy(xpath = ".//input[@name = 'username']")
	private WebElement usernameInput;

	@FindBy(xpath = ".//input[@name = 'email']")
	private WebElement emailInput;

	@FindBy(xpath = ".//input[@name = 'password' and not(@class = 'hide')]")
	private WebElement passwordInput;

	@FindBy(xpath = ".//button[contains(text(), 'Create')]")
	private WebElement createButton;

	@FindBy(xpath = ".//button[contains(text(), 'Save')]")
	private WebElement updateButton;

	@FindBy(xpath = ".//button[contains(text(), 'Delete')]")
	private WebElement deleteButton;

	public CreateUserModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public UserPage registerUser(UserType userType)
	{
		return registerUser(userType.getUsername(), userType.getFirstName(), userType.getLastName(), userType.getEmail(), userType.getPassword());
	}

	public UserPage updateUser(UserType userType)
	{
		return updateUser(userType.getFirstName(), userType.getLastName(), userType.getEmail());
	}

	public UserPage registerUser(String username, String firstName, String lastName, String email, String password)
	{
		usernameInput.sendKeys(username);
		firstNameInput.sendKeys(firstName);
		lastNameInput.sendKeys(lastName);
		emailInput.sendKeys(email);
		passwordInput.sendKeys(password);
		UserPage userPage = clickCreateButton();
		waitUntilModalIsNotPresent();
		return userPage;
	}

	public UserPage updateUser(String firstName, String lastName, String email)
	{
		firstNameInput.sendKeys(firstName);
		lastNameInput.sendKeys(lastName);
		emailInput.sendKeys(email);
		UserPage userPage = clickUpdateButton();
		waitUntilModalIsNotPresent();
		return userPage;
	}

	public UserPage clickCreateButton()
	{
		createButton.click();
		return new UserPage(driver);
	}

	public UserPage clickUpdateButton()
	{
		updateButton.click();
		return new UserPage(driver);
	}

	public void clickDeleteButton()
	{
		deleteButton.click();
		LOGGER.info("Delete button was clicked");
	}

	public WebElement getFirstNameInput()
	{
		return firstNameInput;
	}

	public WebElement getLastNameInput()
	{
		return lastNameInput;
	}

	public WebElement getUsernameInput()
	{
		return usernameInput;
	}

	public WebElement getEmailInput()
	{
		return emailInput;
	}

	public WebElement getPasswordInput()
	{
		return passwordInput;
	}

	public WebElement getCreateButton()
	{
		return createButton;
	}

	public WebElement getUpdateButton()
	{
		return updateButton;
	}

	public WebElement getDeleteButton()
	{
		return deleteButton;
	}
}
