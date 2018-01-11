package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectFilter extends AbstractUIObject
{

	private static final String CONTAINER_LOCATOR = "//div[preceding-sibling::header]/md-menu-content[.//*[text() = 'Clear']]";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	@FindBy(xpath = "//button[following-sibling::div[.//*[text() = 'Clear']]]")
	private WebElement clearButton;

	@FindBy(xpath = "//button[following-sibling::div[.//*[text() = 'Create']]]")
	private WebElement createButton;

	@FindBy(xpath = "//div[preceding-sibling::*[text() = 'chevron_right']]/*[text()]")
	private List<WebElement> projectNames;

	public ProjectFilter(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public void clickProjectByName(String projectName)
	{
		driver.findElement(By.xpath("//button[following-sibling::div[.//*[text() = '" + projectName + "']]]")).click();
	}

	public List<String> getProjectNames()
	{
		return projectNames.stream().map(WebElement::getText).collect(Collectors.toList());
	}

	@Override
	public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
