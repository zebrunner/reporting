package com.qaprosoft.zafira.tests.gui.components.modals;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateTestRunViewModal extends AbstractModalWindow
{

	public static final String TITLE = "View";

	@FindBy(id = "viewName")
	private WebElement nameInput;

	@FindBy(id = "projectName")
	private WebElement projectSelect;

	@FindBy(id = "create")
	private WebElement createButton;

	@FindBy(id = "save")
	private WebElement saveButton;

	@FindBy(id = "delete")
	private WebElement deleteButton;

	public CreateTestRunViewModal(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getNameInput()
	{
		return nameInput;
	}

	public void typeName(String name)
	{
		nameInput.sendKeys(name);
	}

	public WebElement getProjectSelect()
	{
		return projectSelect;
	}

	public void selectProject(String project)
	{
		select(projectSelect, project);
	}

	public WebElement getCreateButton()
	{
		return createButton;
	}

	public void clickCreateButton()
	{
		createButton.click();
	}

	public WebElement getSaveButton()
	{
		return saveButton;
	}

	public void clickSaveButton()
	{
		saveButton.click();
	}

	public WebElement getDeleteButton()
	{
		return deleteButton;
	}

	public void clickDeleteButton()
	{
		deleteButton.click();
	}
}
