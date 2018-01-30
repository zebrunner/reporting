package com.qaprosoft.zafira.tests.gui.components.table.row;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

public abstract class AbstractRow extends AbstractUIObject
{
	protected AbstractRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
