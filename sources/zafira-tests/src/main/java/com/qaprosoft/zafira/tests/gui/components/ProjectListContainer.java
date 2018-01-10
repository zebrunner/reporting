package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectListContainer extends AbstractPage
{

	@FindBy(xpath = "//button[following-sibling::div[.//*[text() = 'Clear']]]")
	private WebElement clearButton;

	@FindBy(xpath = "//button[following-sibling::div[.//*[text() = 'Create']]]")
	private WebElement createButton;

	@FindBy(xpath = "//div[preceding-sibling::*[text() = 'chevron_right']]/*[text()]")
	private List<WebElement> projectNames;

	public ProjectListContainer(WebDriver driver, String path)
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
}
