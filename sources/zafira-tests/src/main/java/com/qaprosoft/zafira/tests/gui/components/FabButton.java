package com.qaprosoft.zafira.tests.gui.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public class FabButton extends AbstractUIObject
{

	@FindBy(xpath = ".//md-fab-trigger")
	private WebElement buttonTrigger;

	@FindBy(xpath = ".//md-fab-actions//button")
	private List<WebElement> buttonsMini;

	public FabButton(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getButtonTrigger()
	{
		return buttonTrigger;
	}

	public List<WebElement> clickButtonTrigger()
	{
		waitUntilElementIsPresent(buttonTrigger, 2);
		buttonTrigger.click();
		pause(2);
		return buttonsMini;
	}

	public List<WebElement> getButtonsMini()
	{
		return buttonsMini;
	}

	public WebElement getButtonMiniByClassName(String className)
	{
		return context.findElement(By.xpath(".//md-fab-actions//button[.//*[contains(@class, '" + className + "')]]"));
	}

	public void clickButtonMiniByClassName(String className)
	{
		pause(2);
		getButtonMiniByClassName(className).click();
	}
}
