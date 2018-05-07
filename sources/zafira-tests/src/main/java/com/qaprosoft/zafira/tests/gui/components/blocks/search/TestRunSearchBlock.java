package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestRunSearchBlock extends AbstractSearchBlock
{

	@FindBy(xpath = "//*[@id = 'searchChechbox']")
	private WebElement mainCheckbox;

	@FindBy(xpath = ".//md-select[@aria-label = 'Status']")
	private WebElement statusSelect;

	@FindBy(id = "search-input")
	private WebElement commonInput;

	@FindBy(xpath = ".//button[text() = 'Test Suite']")
	private WebElement testSuiteInput;

	@FindBy(xpath = ".//button[text() = 'Job URL']")
	private WebElement jobURLInput;

	@FindBy(xpath = ".//md-select[@aria-label = 'Environment']")
	private WebElement environmentSelect;

	@FindBy(xpath = ".//button[text() = 'R']")
	private WebElement reviewedCheckbox;

	@FindBy(xpath = ".//md-select[@aria-label = 'Platform']")
	private WebElement platformSelect;

	@FindBy(xpath = ".//button[text() = ' App Version']")
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
		waitUntilElementToBeClickableByBackdropMask(mainCheckbox, 2);
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
		testSuiteInput.click();
		commonInput.click();
		return commonInput;
	}

	public void typeTestSuiteName(String name)
	{
		if(! isBlank(name)) {
			testSuiteInput.click();
			commonInput.sendKeys(name);
		}
	}

	public WebElement getJobURLInput()
	{
		jobURLInput.click();
		return commonInput;
	}

	public void typeJobURL(String url)
	{
		if(! isBlank(url)) {
			jobURLInput.click();
			commonInput.sendKeys(url);
		}
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
		appVersionInput.click();
		commonInput.click();
		return commonInput;
	}

	public void typeAppVersion(String appVersion)
	{
		if(! isBlank(appVersion)) {
			appVersionInput.click();
			commonInput.sendKeys(appVersion);
		}
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
