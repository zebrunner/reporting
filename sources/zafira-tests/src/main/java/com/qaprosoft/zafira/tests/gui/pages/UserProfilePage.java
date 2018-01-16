package com.qaprosoft.zafira.tests.gui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserProfilePage extends BasePage
{

	@FindBy(xpath = "//button[ancestor::form[@name='access_token_form'] and @type='submit']")
	private WebElement generateTokenButton;

	@FindBy(xpath = "//button[ancestor::form[@name='access_token_form'] and @type='button']")
	private WebElement copyTokenButton;

	@FindBy(xpath = "//input[@name='accessToken']")
	private WebElement tokenInput;

	@FindBy(xpath="//body[contains(@class, 'zaf-light')]")
	private WebElement lightZafiraSchemaStyle;

	@FindBy(xpath="//body[contains(@class, 'zaf-dark')]")
	private WebElement darkZafiraSchemaStyle;

	@FindBy(xpath="//md-radio-button[@value=32]")
	private WebElement lightZafiraSchemaRadioButton;

	@FindBy(xpath="//md-radio-button[@value=22]")
	private WebElement darkZafiraSchemaRadioButton;

	@FindBy(xpath="//md-radio-button[@value=32 and contains(@class, 'md-checked')]")
	private WebElement lightZafiraSchemaRadioButtonChecked;

	@FindBy(xpath="//md-radio-button[@value=22 and contains(@class, 'md-checked')]")
	private WebElement darkZafiraSchemaRadioButtonChecked;

	@FindBy(xpath="//button[ancestor::form[@name='preference_form'] and contains(@class,'md-primary')]")
	private WebElement savePreferencesButton;

	@FindBy(xpath="//button[ancestor::form[@name='preference_form'] and contains(@class,'md-warn')]")
	private WebElement resetPreferencesButton;

	@FindBy(xpath="//md-select[ancestor::form[@name='preference_form'] and @name='defaultDashboard']")
	private WebElement defaultDashboardSelect;

	@FindBy(xpath="//md-select[ancestor::form[@name='preference_form'] and @name='refreshInterval']")
	private WebElement defaultRefreshIntervalSelect;

	@FindBy(xpath="//input[ancestor::form[@name='password_form'] and @name='password']")
	private WebElement passwordInput;

	@FindBy(xpath="//input[ancestor::form[@name='password_form'] and @name='confirmPassword']")
	private WebElement confirmPasswordInput;

	@FindBy(xpath="//button[ancestor::form[@name='password_form'] and @disabled='disabled']")
	private WebElement changePasswordButtonDisabled;

	@FindBy(xpath="//button[ancestor::form[@name='password_form'] and not(@disabled)]")
	private WebElement changePasswordButtonEnabled;

	public enum ColorSchema {LIGHT, DARK};

	public UserProfilePage(WebDriver driver)
	{
		super(driver, "/users/profile");
	}

	public WebElement getGenerateTokenButton()
	{
		return generateTokenButton;
	}

	public WebElement getCopyTokenButton()
	{
		return copyTokenButton;
	}

	public WebElement getTokenInput()
	{
		return tokenInput;
	}

	public WebElement getLightZafiraSchemaStyle()
	{
		return lightZafiraSchemaStyle;
	}

	public WebElement getDarkZafiraSchemaStyle()
	{
		return darkZafiraSchemaStyle;
	}

	public WebElement getLightZafiraSchemaRadioButton()
	{
		return lightZafiraSchemaRadioButton;
	}

	public WebElement getDarkZafiraSchemaRadioButton()
	{
		return darkZafiraSchemaRadioButton;
	}

	public WebElement getLightZafiraSchemaRadioButtonChecked()
	{
		return lightZafiraSchemaRadioButtonChecked;
	}

	public WebElement getDarkZafiraSchemaRadioButtonChecked()
	{
		return darkZafiraSchemaRadioButtonChecked;
	}

	public WebElement getSavePreferencesButton() {
		return savePreferencesButton;
	}

	public WebElement getResetPreferencesButton() {
		return resetPreferencesButton;
	}

	public WebElement getDefaultDashboardSelect() {
		return defaultDashboardSelect;
	}

	public WebElement getDefaultRefreshIntervalSelect() {
		return defaultRefreshIntervalSelect;
	}

	public WebElement getPasswordInput() {
		return passwordInput;
	}

	public WebElement getConfirmPasswordInput() {
		return confirmPasswordInput;
	}

	public WebElement getChangePasswordButtonDisabled() {
		return changePasswordButtonDisabled;
	}

	public WebElement getChangePasswordButtonEnabled() {
		return changePasswordButtonEnabled;
	}
}
