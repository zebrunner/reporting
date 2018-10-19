package com.qaprosoft.zafira.tests.gui.components.table.row;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.table.TestTable;

public class TestRunTableRow extends AbstractRow
{

	@FindBy(xpath = "./td[1]//small")
	private WebElement percentage;

	@FindBy(xpath = "./td[1]//md-progress-circular")
	private WebElement progressCircularIcon;

	@FindBy(xpath = "./td[1]//md-checkbox")
	private WebElement checkbox;

	@FindBy(xpath = "./td[2]//b")
	private WebElement testRunName;

	@FindBy(xpath = "./td[2]//md-icon")
	private WebElement commentIcon;

	@FindBy(xpath = "./td[2]//span[text() = 'R']")
	private WebElement reviewedLabel;

	@FindBy(xpath = "./td[2]//a")
	private WebElement jobLink;

	@FindBy(xpath = "./td[2]//small")
	private WebElement appVersion;

	@FindBy(xpath = "./td[3]//span")
	private WebElement environment;

	@FindBy(xpath = "./td[2]//i")
	private WebElement expandTestsIcon;

	@FindBy(xpath = "//*[@id = 'test-run-background']//*[contains(@class, 'expand-button')]")
	private WebElement closeTestsIcon;

	@FindBy(xpath = "./td[4]//span")
	private WebElement platformIcon;

	@FindBy(xpath = "./td[5]/span[1]")
	private WebElement passed;

	@FindBy(xpath = "./td[5]/span[2]")
	private WebElement failed;

	@FindBy(xpath = "./td[5]/span[2]/span[1]")
	private WebElement knownIssues;

	@FindBy(xpath = "./td[5]/span[2]/span[2]")
	private WebElement blockers;

	@FindBy(xpath = "./td[5]/span[3]")
	private WebElement skipped;

	@FindBy(xpath = "./td[5]/span[4]")
	private WebElement inProgress;

	@FindBy(xpath = "./td[6]//time")
	private WebElement elapsedTime;

	@FindBy(xpath = "./td[7]//md-menu")
	private TestRunSettingMenu testRunSettingMenu;

	@FindBy(xpath = "./following-sibling::tr[1]//table")
	private TestTable testTable;

	public TestRunTableRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public Status getTestRunStatus()
	{
		String[] classes = this.getRootElement().getAttribute("class").split(" ");
		return Status.valueOf(classes[classes.length - 1].toUpperCase());
	}

	public WebElement getPercentage()
	{
		return percentage;
	}

	public String getPercentageText() {
		return percentage.getText();
	}

	public WebElement getProgressCircularIcon()
	{
		return progressCircularIcon;
	}

	public WebElement getCheckbox()
	{
		return checkbox;
	}

	public void checkCheckbox()
	{
		if(isElementPresent(checkbox, 1))
		{
			check(checkbox);
		} else
		{
			progressCircularIcon.click();
		}
	}

	public void uncheckCheckbox()
	{
		uncheck(checkbox);
	}

	public WebElement getTestRunName()
	{
		return testRunName;
	}

	public String getTestRunNameText()
	{
		return getCurrentNodeText(testRunName, true);
	}

	public String getTestSuiteFileName()
	{
		return hoverAndGetTooltipText(testRunName);
	}

	public WebElement getCommentIcon()
	{
		return commentIcon;
	}

	public void clickCommentIcon()
	{
		commentIcon.click();
	}

	public WebElement getReviewedLabel()
	{
		return reviewedLabel;
	}

	public WebElement getJobLink()
	{
		return jobLink;
	}

	public String getJobLinkText()
	{
		return jobLink.getText();
	}

	public void clickJobLink()
	{
		jobLink.click();
	}

	public WebElement getAppVersion()
	{
		return appVersion;
	}

	public String getAppVersionText()
	{
		return appVersion.getText().split("insert_drive_file")[1];
	}

	public WebElement getEnvironment()
	{
		return environment;
	}

	public String getEnvironmentText()
	{
		return environment.getText();
	}

	public WebElement getExpandTestsIcon()
	{
		return expandTestsIcon;
	}

	public WebElement getCloseTestsIcon() {
		return closeTestsIcon;
	}

	public TestTable clickExpandTestsIcon()
	{
		expandTestsIcon.click();
		return testTable;
	}

	public TestTable clickCloseTestIcon() {
		closeTestsIcon.click();
		return testTable;
	}

	public WebElement getPlatformIcon()
	{
		return platformIcon;
	}

	public String getPlatform()
	{
		String[] classes = platformIcon.getAttribute("class").split(" ");
		return classes[classes.length - 1].toLowerCase();
	}

	public WebElement getPassed()
	{
		return passed;
	}

	public Integer getPassedCount()
	{
		return Integer.valueOf(passed.getText());
	}

	public WebElement getFailed()
	{
		return failed;
	}

	public Integer getFailedCount()
	{
		return Integer.valueOf(failed.getText().split(" ")[0]);
	}

	public WebElement getKnownIssues()
	{
		return knownIssues;
	}

	public Integer getKnownIssuesCount()
	{
		return Integer.valueOf(knownIssues.getText().split("\\| ")[1]);
	}

	public WebElement getBlockers()
	{
		return blockers;
	}

	public Integer getBlockersCount()
	{
		return Integer.valueOf(blockers.getText().split("\\| ")[1]);
	}

	public WebElement getSkipped()
	{
		return skipped;
	}

	public Integer getSkippedCount()
	{
		return Integer.valueOf(skipped.getText());
	}

	public WebElement getInProgress()
	{
		return inProgress;
	}

	public Integer getInProgressCount()
	{
		return Integer.valueOf(inProgress.getText());
	}

	public WebElement getElapsedTime()
	{
		return elapsedTime;
	}

	public String getElapsedTimeText()
	{
		return elapsedTime.getText();
	}

	public TestRunSettingMenu getTestRunSettingMenu()
	{
		return testRunSettingMenu;
	}

	public TestRunSettingMenu clickTestRunSettingMenu()
	{
		testRunSettingMenu.getRootElement().click();
		pause(0.5);
		waitUntilElementToBeClickableWithBackdropMask(testRunSettingMenu.getSendAsEmailButton(), 5);
		return testRunSettingMenu;
	}

	public TestTable getTestTable()
	{
		return testTable;
	}
}
