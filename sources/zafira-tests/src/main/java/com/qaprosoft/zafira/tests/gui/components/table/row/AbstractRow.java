package com.qaprosoft.zafira.tests.gui.components.table.row;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public abstract class AbstractRow extends AbstractUIObject
{
	protected AbstractRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
