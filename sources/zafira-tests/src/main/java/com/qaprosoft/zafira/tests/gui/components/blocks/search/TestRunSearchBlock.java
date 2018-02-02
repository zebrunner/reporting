package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestRunSearchBlock extends AbstractSearchBlock
{

	@FindBy(xpath = ".//md-checkbox")
	private WebElement mainCheckbox;

	@FindBy(xpath = ".//md-select[@placeholder = 'Status']")
	private WebElement statusSelect;

	@FindBy(xpath = ".//input[preceding-sibling::label[text() = 'Test suite']]")
	private WebElement testSuiteInput;

	@FindBy(xpath = ".//input[preceding-sibling::label[text() = 'Job URL']]")
	private WebElement jobURLInput;

	@FindBy(xpath = ".//md-select[@placeholder = 'Environment']")
	private WebElement environmentSelect;

	@FindBy(xpath = ".//span[text() = 'R']")
	private WebElement reviewedCheckbox;

	@FindBy(xpath = ".//md-select[@placeholder = 'Platform']")
	private WebElement platformSelect;

	@FindBy(xpath = ".//input[preceding-sibling::label[text() = 'App version']]")
	private WebElement appVersionInput;

	@FindBy(xpath = ".//button[.//*[text() = 'today']]")
	private WebElement dateButton;

	public TestRunSearchBlock(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getMainCheckbox()
	{
		return mainCheckbox;
	}

	public void checkMainCheckbox()
	{
		check(mainCheckbox);
	}

	public void uncheckMainCheckbox()
	{
		uncheck(mainCheckbox);
	}

	public WebElement getStatusSelect()
	{
		return statusSelect;
	}

	public WebElement getTestSuiteInput()
	{
		return testSuiteInput;
	}

	public void typeTestSuiteName(String name)
	{
		testSuiteInput.sendKeys(name);
	}

	public WebElement getJobURLInput()
	{
		return jobURLInput;
	}

	public void typeJobURL(String url)
	{
		jobURLInput.sendKeys(url);
	}

	public WebElement getEnvironmentSelect()
	{
		return environmentSelect;
	}

	public WebElement getReviewedCheckbox()
	{
		return reviewedCheckbox;
	}

	public void clickReviewedCheckbox()
	{
		reviewedCheckbox.click();
	}

	public WebElement getPlatformSelect()
	{
		return platformSelect;
	}

	public WebElement getAppVersionInput()
	{
		return appVersionInput;
	}

	public void typeAppVersion(String appVersion)
	{
		appVersionInput.sendKeys(appVersion);
	}

	public WebElement getDateButton()
	{
		return dateButton;
	}

	public void clickDateButton()
	{
		dateButton.click();
	}
}
