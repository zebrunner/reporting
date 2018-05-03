package com.qaprosoft.zafira.tests.gui.components.table.row;

import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.tests.gui.components.menus.TestArtifactMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.TestDetailsModalWindow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestRow extends AbstractRow
{

	@FindBy(xpath = "./td[1]/b")
	private WebElement testName;

	@FindBy(xpath = "./td[1]/b/span[text() = 'BLOCKER']")
	private WebElement blockerLabel;

	@FindBy(xpath = "./td[1]//timer")
	private WebElement elapsedTime;

	@FindBy(xpath = "./td[1]//span[2]")
	private WebElement owner;

	@FindBy(xpath = "./td[1]//span[3]")
	private WebElement device;

	@FindBy(xpath = ".//*[contains(text(), 'more')]")
	private WebElement showMoreLink;

	@FindBy(xpath = ".//*[contains(text(), 'less')]")
	private WebElement showLess;

	@FindBy(xpath = ".//*[@name = 'errorMsg']/*")
	private List<WebElement> logs;

	@FindBy(xpath = "./td[4]//button[contains(md-icon, 'forum')]")
	private WebElement openTestDetailsModalButton;

	@FindBy(xpath = "./td[3]//a[contains(@class, 'bug-label-bg')]")
	private WebElement knownIssueLabel;

	@FindBy(xpath = "./td[3]//a[not(contains(@class, 'bug-label-bg'))]")
	private WebElement taskLabel;

	@FindBy(xpath = "./td[4]//button[contains(md-icon, 'attachment')]")
	private WebElement testArtifactMenuButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content")
	private TestArtifactMenu testArtifactMenu;

	@FindBy(xpath = "//md-dialog")
	private TestDetailsModalWindow testDetailsModalWindow;

	public TestRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public Status getStatus()
	{
		String[] classes = getRootElement().getAttribute("class").split(" ");
		String statusClass = classes[classes.length - 1].toLowerCase();
		Status result = null;
		switch (statusClass)
		{
			case "success":
				result = Status.PASSED;
				break;
			case "danger":
				result = Status.FAILED;
				break;
			case "warning":
				result = Status.SKIPPED;
				break;
			case "info":
				result = Status.IN_PROGRESS;
				break;
			default:
				result = Status.ABORTED;
				break;
		}
		return result;
	}

	public WebElement getTestName()
	{
		return testName;
	}

	public String getTestNameText()
	{
		return testName.getText();
	}

	public WebElement getBlockerLabel()
	{
		return blockerLabel;
	}

	public WebElement getElapsedTime()
	{
		return elapsedTime;
	}

	public String getElapsedTimeText()
	{
		return elapsedTime.getText();
	}

	public WebElement getOwner()
	{
		return owner;
	}

	public String getOwnerName()
	{
		return owner.getText();
	}

	public WebElement getDevice()
	{
		return device;
	}

	public String getDeviceName()
	{
		return device.getText();
	}

	public WebElement getShowMoreLink()
	{
		return showMoreLink;
	}

	public void clickShowMoreLink()
	{
		showMoreLink.click();
	}

	public void clickShowLessLink()
	{
		showLess.click();
	}

	public WebElement getShowLess()
	{
		return showLess;
	}

	public List<WebElement> getLogs()
	{
		return logs;
	}

	public String getShowMoreLogText()
	{
		return getCurrentNodeText(logs.get(0));
	}

	public String getShowLessLogText()
	{
		return logs.get(1).getText();
	}

	public WebElement getOpenTestDetailsModalButton()
	{
		return openTestDetailsModalButton;
	}

	public TestDetailsModalWindow clickOpenTestDetailsModalButton()
	{
		openTestDetailsModalButton.click();
		waitUntilElementToBeClickableByBackdropMask(testDetailsModalWindow.getCloseButton(), 2);
		return testDetailsModalWindow;
	}

	public WebElement getKnownIssueLabel()
	{
		return knownIssueLabel;
	}

	public String getKnownIssueTicket()
	{
		return knownIssueLabel.getText();
	}

	public WebElement getTaskLabel()
	{
		return taskLabel;
	}

	public String getTaskTicket()
	{
		return taskLabel.getText();
	}

	public WebElement getTestArtifactMenuButton()
	{
		return testArtifactMenuButton;
	}

	public TestArtifactMenu clickTestArtifactButton()
	{
		testArtifactMenuButton.click();
		return testArtifactMenu;
	}

	public TestArtifactMenu getTestArtifactMenu()
	{
		return testArtifactMenu;
	}

	public TestDetailsModalWindow getTestDetailsModalWindow()
	{
		return testDetailsModalWindow;
	}
}
