package com.qaprosoft.zafira.tests.gui.components.menus;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractMenu extends AbstractUIObject
{

	protected AbstractMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
