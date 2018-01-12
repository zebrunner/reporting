package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

public class TestRunPage extends AbstractPage
{
	public TestRunPage(WebDriver driver)
	{
		super(driver, "/tests/runs");
	}
}
