package com.qaprosoft.zafira.tests.gui.components.menus;

import com.qaprosoft.zafira.tests.gui.components.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractMenu extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//div[preceding-sibling::header]/md-menu-content";

	@FindBy(xpath = CONTAINER_LOCATOR)
	protected WebElement container;

	protected AbstractMenu(WebDriver driver, String path)
	{
		super(driver, path);
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
