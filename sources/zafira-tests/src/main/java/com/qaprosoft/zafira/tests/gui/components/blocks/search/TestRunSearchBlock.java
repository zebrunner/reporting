package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import org.apache.commons.lang3.StringUtils;
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

	public void selectStatus(String status)
	{
		if(! isBlank(status))
			select(statusSelect, status);
	}

	public WebElement getTestSuiteInput()
	{
		return testSuiteInput;
	}

	public void typeTestSuiteName(String name)
	{
		if(! isBlank(name))
			testSuiteInput.sendKeys(name);
	}

	public WebElement getJobURLInput()
	{
		return jobURLInput;
	}

	public void typeJobURL(String url)
	{
		if(! isBlank(url))
			jobURLInput.sendKeys(url);
	}

	public WebElement getEnvironmentSelect()
	{
		return environmentSelect;
	}

	public void selectEnvironment(String environment)
	{
		if(! isBlank(environment))
			select(environmentSelect, environment);
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

	public void selectPlatform(String platform)
	{
		if(! isBlank(platform))
			select(platformSelect, platform);
	}

	public WebElement getAppVersionInput()
	{
		return appVersionInput;
	}

	public void typeAppVersion(String appVersion)
	{
		if(! isBlank(appVersion))
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
