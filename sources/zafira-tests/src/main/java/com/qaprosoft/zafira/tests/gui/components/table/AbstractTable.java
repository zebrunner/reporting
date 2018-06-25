package com.qaprosoft.zafira.tests.gui.components.table;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public abstract class AbstractTable extends AbstractUIObject
{
	protected AbstractTable(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
