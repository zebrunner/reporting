package com.qaprosoft.zafira.tests.gui.components.table.row;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.table.TestTable;

public class TestRunTableRow extends AbstractRow
{

	private static final String TABLE_ROW_CELL_CLASS_NAME = "test-run-card__cell";
	private static final String TABLE_ROW_CELL = ".//*[contains(@class, '" + TABLE_ROW_CELL_CLASS_NAME + "')]";

	@FindBy(xpath = TABLE_ROW_CELL + "[1]/small")
	private WebElement percentage;

	@FindBy(xpath = TABLE_ROW_CELL + "[1]//md-progress-circular")
	private WebElement progressCircularIcon;

	@FindBy(xpath = TABLE_ROW_CELL + "[1]//md-checkbox")
	private WebElement checkbox;

	@FindBy(className = "test-run-card__title")
	private WebElement testRunName;

	@FindBy(className = "_comments")
	private WebElement commentIcon;

	@FindBy(className = "_reviewed")
	private WebElement reviewedLabel;

	@FindBy(css = ".test-run-card__job-name a")
	private WebElement jobLink;

	@FindBy(className = "test-run-card__app-name")
	private WebElement appVersion;

	@FindBy(css = "._env span")
	private WebElement environment;

	@FindBy(className = ".test-run-card__clickable")
	private WebElement openTestRunElement;

	@FindBy(xpath = "//*[contains(@class, 'test-run-card__back-btn')]")
	private WebElement closeTestsIcon;

	@FindBy(css = "._platform span")
	private WebElement platformIcon;

	@FindBy(css = "._statistics .label-success-border")
	private WebElement passed;

	@FindBy(css = "._statistics .label-danger-border")
	private WebElement failed;

	@FindBy(css = "._statistics .label-danger-border")
	private WebElement knownIssues;

	@FindBy(css = "._statistics .label-danger-border")
	private WebElement blockers;

	@FindBy(css = "._statistics .label-warning-border")
	private WebElement skipped;

	@FindBy(css = "._statistics .label-info-border")
	private WebElement inProgress;

	@FindBy(css = "._date time")
	private WebElement elapsedTime;

	@FindBy(css = "._menu md-menu")
	private TestRunSettingMenu testRunSettingMenu;

	@FindBy(xpath = "//table")
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
		return appVersion.getText();
	}

	public WebElement getEnvironment()
	{
		return environment;
	}

	public String getEnvironmentText()
	{
		return environment.getText();
	}

	public WebElement getCloseTestsIcon() {
		return closeTestsIcon;
	}

	public TestTable expandRun()
	{
		pause(2);
		WebElement row = testRunName.findElement(By.xpath("./ancestor::*[contains(@class, 'test-run-card ')]"));
		clickByCoordinates(row, 150, 5);
		pause(2);
		return new TestTable(driver, driver);
	}

	public TestTable clickCloseTestIcon() {
		driver.findElement(By.className("test-run-card__back-btn")).click();
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
		return Integer.valueOf(failed.getText().split("\n\\|\n")[0]);
	}

	public WebElement getKnownIssues()
	{
		return knownIssues;
	}

	public Integer getKnownIssuesCount()
	{
		return Integer.valueOf(knownIssues.getText().split("\n\\|\n")[1]);
	}

	public WebElement getBlockers()
	{
		return blockers;
	}

	public Integer getBlockersCount()
	{
		return Integer.valueOf(blockers.getText().split("\n\\|\n")[2]);
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
