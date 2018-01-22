package com.qaprosoft.zafira.tests.gui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UserProfilePage extends BasePage
{

	@FindBy(xpath="//input[@name='username' and @disabled]")
	private WebElement userNameInputDisabled;

	@FindBy(xpath="//input[@name='firstName']")
	private WebElement firstNameInput;

	@FindBy(xpath="//input[@name='lastName']")
	private WebElement lastNameInput;

	@FindBy(xpath="//input[@name='email']")
	private WebElement emailInput;

	@FindBy(xpath="//div[contains(text(),'ROLE_USER')]")
	private WebElement roleUserLabel;

	@FindBy(xpath="//div[contains(text(),'ROLE_ADMIN')]")
	private WebElement roleAdminLabel;




	@FindBy(xpath="//md-option[@value='General']")
	private WebElement generalBoardButton;

	@FindBy(xpath="//md-option[@value='General' and @selected]")
	private WebElement generalBoardButtonSelected;

	@FindBy(xpath="//md-option[contains(@value,'Nightly')]")
	private WebElement nightlyBoardButton;








	@FindBy(xpath="//button[ancestor::form[@name='profile_form'] and not(@disabled)]")
	private WebElement saveUserProfileButtonEnabled;

	@FindBy(xpath="//button[ancestor::form[@name='profile_form'] and @disabled]")
	private WebElement saveUserProfileButtonDisabled;

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

	@FindBy(xpath = "//button[ancestor::form[@name='access_token_form'] and @type='submit']")
	private WebElement generateTokenButton;

	@FindBy(xpath = "//button[ancestor::form[@name='access_token_form'] and @type='button']")
	private WebElement copyTokenButton;

	@FindBy(xpath = "//input[@name='accessToken']")
	private WebElement tokenInput;


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

	public WebElement getUserNameInputDisabled() {
		return userNameInputDisabled;
	}

	public WebElement getFirstNameInput() {
		return firstNameInput;
	}

	public WebElement getLastNameInput() {
		return lastNameInput;
	}

	public WebElement getEmailInput() {
		return emailInput;
	}

	public WebElement getRoleUserLabel() {
		return roleUserLabel;
	}

	public WebElement getRoleAdminLabel() {
		return roleAdminLabel;
	}

	public WebElement getSaveUserProfileButtonEnabled() {
		return saveUserProfileButtonEnabled;
	}

	public WebElement getSaveUserProfileButtonDisabled() {
		return saveUserProfileButtonDisabled;
	}

	public WebElement getGeneralBoardButton() {
		return generalBoardButton;
	}

	public WebElement getNightlyBoardButton() {
		return nightlyBoardButton;
	}

	public WebElement getGeneralBoardButtonSelected() {
		return generalBoardButtonSelected;
	}
}
