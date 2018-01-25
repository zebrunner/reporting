package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractUIObject extends AbstractPage implements IElement
{
	protected AbstractUIObject(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public boolean isElementPresent(long timeout)
	{
		return isElementPresent(getLocator(), timeout);
	}

	public void clearAllInputs()
	{
		getElement().findElements(By.xpath(".//input[not(@type = 'checkbox') and not(@disabled)]")).forEach(WebElement::clear);
	}
}
