package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

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
}
