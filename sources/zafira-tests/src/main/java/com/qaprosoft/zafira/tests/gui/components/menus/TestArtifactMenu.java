package com.qaprosoft.zafira.tests.gui.components.menus;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestArtifactMenu extends AbstractMenu
{

	@FindBy(xpath = ".//a")
	private List<WebElement> artifacts;

	public TestArtifactMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public List<WebElement> getArtifacts()
	{
		return artifacts;
	}

	public WebElement getArtifactByName(String name)
	{
		return artifacts.stream().filter(artifact -> artifact.getText().equals(name)).findFirst().orElse(null);
	}

	public void clickArtifactByName(String name)
	{
		getArtifactByName(name).click();
	}

	public void clickLogArtifact()
	{
		clickArtifactByName("Log");
	}
}
