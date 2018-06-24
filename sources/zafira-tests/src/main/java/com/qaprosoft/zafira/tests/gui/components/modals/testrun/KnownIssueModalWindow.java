package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;

public class KnownIssueModalWindow extends AbstractModalWindow
{
	@FindBy(xpath = ".//input[preceding-sibling::label[text() = 'Jira ID']]")
	private WebElement jiraIdInput;

	@FindBy(xpath = ".//textarea")
	private WebElement descriptionInput;

	@FindBy(xpath = ".//md-checkbox")
	private WebElement blockerCheckbox;


	@FindBy(xpath = ".//*[@name = 'knownIssue']")
	private List<WebElement> knownIssuesHistoryItems;

	@FindBy(xpath = ".//button[contains(text(), 'Create')]")
	private WebElement createButton;

	@FindBy(xpath = ".//button[contains(text(), 'Clear')]")
	private WebElement clearButton;

	@FindBy(xpath = ".//button[contains(text(), 'Update')]")
	private WebElement updateButton;

	public KnownIssueModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getJiraIdInput()
	{
		return jiraIdInput;
	}

	public void typeJiraId(String jiraId)
	{
		this.jiraIdInput.sendKeys(jiraId);
	}

	public WebElement getDescriptionInput()
	{
		return descriptionInput;
	}

	public void typeDescription(String description)
	{
		this.descriptionInput.sendKeys(description);
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

	public WebElement getCreateButton()
	{
		return createButton;
	}

	public void clickCreateButton()
	{
		this.createButton.click();
	}

	public WebElement getClearButton()
	{
		return clearButton;
	}

	public void clickClearButton()
	{
		this.clearButton.click();
	}

	public WebElement getUpdateButton()
	{
		return updateButton;
	}

	public void clickUpdateButton()
	{
		this.updateButton.click();
	}
}
