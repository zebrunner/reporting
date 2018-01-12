package com.qaprosoft.zafira.tests.services.gui;

import org.openqa.selenium.WebDriver;

public abstract class AbstractPageService
{

	protected WebDriver driver;

	protected AbstractPageService(WebDriver driver)
	{
		this.driver = driver;
	}
}
