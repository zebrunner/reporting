package com.qaprosoft.zafira.tests.gui.components.menus;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public abstract class AbstractMenu extends AbstractUIObject
{

	protected AbstractMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
