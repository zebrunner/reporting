package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;

public class TaskModalWindow extends AbstractModalWindow
{

	@FindBy(id = "jiraId")
	private WebElement jiraIdInput;

	@FindBy(id = "description")
	private WebElement descriptionTextarea;

	@FindBy(id = "assign")
	private WebElement assignButton;

	@FindBy(id = "delete")
	public WebElement deleteButton;

	@FindBy(id = "update")
	public WebElement updateButton;

	public TaskModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getJiraIdInput()
	{
		return jiraIdInput;
	}

	public void typeJiraId(String jiraId)
	{
		jiraIdInput.sendKeys(jiraId);
	}

	public WebElement getDescriptionTextarea()
	{
		return descriptionTextarea;
	}

	public void typeDescription(String description)
	{
		descriptionTextarea.sendKeys(description);
	}

	public WebElement getAssignButton()
	{
		return assignButton;
	}

	public void clickAssignButton()
	{
		assignButton.click();
	}

	public WebElement getDeleteButton()
	{
		return deleteButton;
	}

	public void clickDeleteButton()
	{
		deleteButton.click();
	}

	public WebElement getUpdateButton()
	{
		return updateButton;
	}

	public void clickUpdateButton()
	{
		updateButton.click();
	}
}
