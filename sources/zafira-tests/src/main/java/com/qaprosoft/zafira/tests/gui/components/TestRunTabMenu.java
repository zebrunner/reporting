package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestRunTabMenu extends AbstractUIObject
{
	private static final String CONTAINER_LOCATOR = "//*[@id  ='nav']//ul[preceding-sibling::a[.//*[text()='Test runs']]]";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	protected TestRunTabMenu(WebDriver driver, String path)
	{
		super(driver, path);
	}

	@Override public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override public WebElement getElement()
	{
		return this.container;
	}
}
