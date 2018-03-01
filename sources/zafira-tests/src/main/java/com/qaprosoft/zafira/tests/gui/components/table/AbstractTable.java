package com.qaprosoft.zafira.tests.gui.components.table;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

public abstract class AbstractTable extends AbstractUIObject
{
	protected AbstractTable(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
