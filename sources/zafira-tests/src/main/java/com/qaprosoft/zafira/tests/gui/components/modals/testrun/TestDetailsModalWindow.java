package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestDetailsModalWindow extends AbstractModalWindow
{

	@FindBy(xpath = ".//md-progress-linear")
	private WebElement progressLinear;

	@FindBy(xpath = ".//h2[contains(text(), 'Test info')]")
	private WebElement statusTabHeading;

	@FindBy(xpath = ".//h2[contains(text(), 'Assign known issue')]")
	private WebElement issuesTabHeading;

	@FindBy(xpath = ".//h2[contains(text(), 'Assign task')]")
	private WebElement tasksTabHeading;

	@FindBy(xpath = ".//h2[contains(text(), 'Comments')]")
	private WebElement commentsTabHeading;

	@FindBy(xpath = ".//md-tab-item[contains(text(), 'Status')]")
	private WebElement statusTab;

	@FindBy(xpath = ".//md-tab-item[contains(text(), 'Issues')]")
	private WebElement issuesTab;

	@FindBy(xpath = ".//md-tab-item[contains(text(), 'Tasks')]")
	private WebElement tasksTab;

	@FindBy(xpath = ".//md-tab-item[contains(text(), 'Comments')]")
	private WebElement commentsTab;

	@FindBy(xpath = ".//textarea[@id = 'issueDescription']")
	private WebElement issueTextArea;

	@FindBy(xpath = ".//textarea[@id = 'taskDescription']")
	private WebElement taskTextArea;

	@FindBy(xpath = ".//input[@id = 'issueJiraId']")
	private WebElement issueInput;

	@FindBy(xpath = ".//input[@id = 'taskJiraId']")
	private WebElement taskInput;

	@FindBy(xpath = ".//textarea[@id = 'comment']")
	private WebElement commentTextArea;

	@FindBy(xpath = ".//md-checkbox")
	private WebElement blockerCheckbox;

	@FindBy(xpath = ".//*[@name = 'knownIssue']")
	private List<WebElement> knownIssuesHistoryItems;

	@FindBy(xpath = ".//div[@id = 'historicalIssues']")
	private WebElement issuesListControl;

	@FindBy(xpath = ".//div[@id = 'historicalTasks']")
	private WebElement tasksListControl;

	@FindBy(xpath = ".//button[contains(@class, 'md-sidemenu-button')]")
	private List<WebElement> issuesListItems;

	@FindBy(xpath = ".//button[contains(@class, 'md-sidemenu-button')]")
	private List<WebElement> taskListItems;

	@FindBy(xpath = ".//button//a[contains(text(), 'Comments')]")
	private WebElement commentsListButton;

	@FindBy(xpath = ".//button[contains(md-icon, 'comment')]")
	private WebElement commentButton;

	@FindBy(xpath = ".//button[contains(md-icon, 'edit')]")
	private WebElement editButton;

	@FindBy(xpath = ".//button[contains(text(), 'Add comment')]")
	private WebElement addCommentButton;

	@FindBy(xpath = ".//button[contains(text(), 'Save')]")
	private WebElement saveButton;

	@FindBy(xpath = ".//button[@id = 'assignIssue']")
	private WebElement assignIssueButton;

	@FindBy(xpath = ".//button[@id = 'assignTask']")
	private WebElement assignTaskButton;

	@FindBy(xpath = ".//button[@id = 'unassignIssue']")
	private WebElement unassignIssueButton;

	@FindBy(xpath = ".//button[@id = 'unassignTask']")
	private WebElement unassignTaskButton;

	@FindBy(xpath = ".//button[@id = 'updateIssue']")
	private WebElement updateIssueButton;

	@FindBy(xpath = ".//button[@id = 'updateTask']")
	private WebElement updateTaskButton;

	@FindBy(xpath = ".//md-select[@ng-if]")
	private WebElement changeStatusSelect;

	public TestDetailsModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getProgressLinear()
	{
		return progressLinear;
	}

	public WebElement getStatusTabHeading()
	{
		return statusTabHeading;
	}

	public WebElement getIssuesTabHeading()
	{
		return issuesTabHeading;
	}

	public WebElement getTasksTabHeading()
	{
		return tasksTabHeading;
	}

	public WebElement getCommentsTabHeading()
	{
		return commentsTabHeading;
	}

	public WebElement getSaveButton()
	{
		return saveButton;
	}

	public void clickSaveButton()
	{
		this.saveButton.click();
	}

	public void typeIssueJiraId(String jiraId)
	{
		this.issueInput.sendKeys(jiraId);
	}

	public void typeTaskJiraId(String jiraId)
	{
		this.taskInput.sendKeys(jiraId);
	}

	public WebElement getCommentTextArea()
	{
		return commentTextArea;
	}

	public void typeComment(String description)
	{
		this.commentTextArea.sendKeys(description);
	}

	public void typeIssueDescription(String description)
	{
		this.issueTextArea.sendKeys(description);
	}

	public void typeTaskDescription(String description)
	{
		this.taskTextArea.sendKeys(description);
	}

	public WebElement getBlockerCheckbox()
	{
		return blockerCheckbox;
	}

	public void checkBlockerCheckbox()
	{
		check(this.blockerCheckbox);
	}

	public void uncheckBlockerCheckbox()
	{
		uncheck(this.blockerCheckbox);
	}

	public List<WebElement> getKnownIssuesHistoryItems()
	{
		return knownIssuesHistoryItems;
	}

	public WebElement getAssignIssueButton()
	{
		return assignIssueButton;
	}

	public void clickAssignIssueButton()
	{
		this.assignIssueButton.click();
	}

	public WebElement getAssignTaskButton()
	{
		return assignTaskButton;
	}

	public void clickAssignTaskButton()
	{
		this.assignTaskButton.click();
	}

	public WebElement getUnassignIssueButton()
	{
		return unassignIssueButton;
	}

	public void clickUnassignIssueButton()
	{
		this.unassignIssueButton.click();
	}

	public WebElement getUnassignTaskButton()
	{
		return unassignTaskButton;
	}

	public void clickUnassignTaskButton()
	{
		this.unassignTaskButton.click();
	}

	public WebElement getUpdateIssueButton()
	{
		return updateIssueButton;
	}

	public void clickUpdateIssueButton()
	{
		this.updateIssueButton.click();
	}

	public WebElement getUpdateTaskButton()
	{
		return updateTaskButton;
	}

	public void clickUpdateTaskButton()
	{
		this.updateTaskButton.click();
	}

	public WebElement getCommentButton()
	{
		return commentButton;
	}

	public void clickCommentButton()
	{
		this.commentButton.click();
	}

	public WebElement getEditButton()
	{
		return editButton;
	}

	public void clickEditButton()
	{
		this.editButton.click();
	}

	public WebElement getCommentsListButton()
	{
		return commentsListButton;
	}

	public void clickCommentsListButton()
	{
		this.commentsListButton.click();
	}

	public WebElement getAddCommentButton()
	{
		return addCommentButton;
	}

	public void clickAddCommentButton()
	{
		this.addCommentButton.click();
	}

	public WebElement getIssuesListControl()
	{
		return issuesListControl;
	}

	public void clickIssuesListControl()
	{
		this.issuesListControl.click();
	}

	public WebElement getTasksListControl()
	{
		return tasksListControl;
	}

	public void clickTasksListControl()
	{
		this.tasksListControl.click();
	}

	public List<WebElement> getTaskListItems()
	{
		return taskListItems;
	}

	public WebElement getStatusTab()
	{
		return statusTab;
	}

	public void clickStatusTab()
	{
		this.statusTab.click();
	}

	public WebElement getIssuesTab()
	{
		return issuesTab;
	}

	public void clickIssuesTab()
	{
		this.issuesTab.click();
	}

	public WebElement getTasksTab()
	{
		return tasksTab;
	}

	public void clickTasksTab()
	{
		this.tasksTab.click();
	}

	public WebElement getCommentsTab()
	{
		return commentsTab;
	}

	public void clickCommentsTab()
	{
		this.commentsTab.click();
	}

	public WebElement getChangeStatusSelect()
	{
		return changeStatusSelect;
	}

	public WebElement getIssueTextArea()
	{
		return issueTextArea;
	}

	public WebElement getTaskTextArea()
	{
		return taskTextArea;
	}

	public WebElement getIssueInput()
	{
		return issueInput;
	}

	public WebElement getTaskInput()
	{
		return taskInput;
	}

	public List<WebElement> getTestStatuses(){
		return driver.findElements(By.xpath("//md-option[contains(@ng-repeat,'status')]"));
	}

	public List<WebElement> getIssuesListItems()
	{
		return issuesListItems;
	}

	public void clearAllModalInputs()
	{
		List <WebElement> workItems = getRootElement().findElements(By.xpath(".//input[not(@type = 'checkbox') and not(@disabled)] | .//textarea"));
		System.out.println(workItems);
		getRootElement().findElements(By.xpath(".//input[ancestor::md-dialog and not(@type = 'checkbox') and not(@disabled)] | .//textarea")).forEach(input -> {
			if(this.isElementPresent(input,1)){
				while(! input.getAttribute("value").isEmpty())
				{
					input.click();
					input.sendKeys(Keys.BACK_SPACE);
				}
			}
		});
	}



}
