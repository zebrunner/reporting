package com.qaprosoft.zafira.tests.gui.components;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public class ProjectFilterMenu extends AbstractUIObject
{

	@FindBy(xpath = ".//button[following-sibling::div[.//*[text() = 'Clear']]]")
	private WebElement clearButton;

	@FindBy(xpath = ".//button[following-sibling::div[.//*[text() = 'Create']]]")
	private WebElement createButton;

	@FindBy(xpath = ".//md-list-item[@name='projectName']//div[@class='md-list-item-text']")
	private List<WebElement> projectNames;

	public ProjectFilterMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public void clickProjectByName(String projectName)
	{
		context.findElement(By.xpath("//button[following-sibling::div[.//*[text() = '" + projectName + "']]]")).click();
	}

	public List<String> getProjectNames()
	{
		return projectNames.stream().map(webElement -> webElement.getText().toUpperCase()).collect(Collectors.toList());
	}

	public WebElement getClearButton()
	{
		return clearButton;
	}

	public WebElement getCreateButton()
	{
		return createButton;
	}
}
