package com.qaprosoft.zafira.tests.services.gui;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.util.Config;

public abstract class AbstractPageService
{
	private static final Logger LOGGER = Logger.getLogger(AbstractPageService.class);

	protected int GENERAL_DASHBOARD_ID = Integer.valueOf(Config.get("dashboard.general.id"));
	protected WebDriver driver;

	protected AbstractPageService(WebDriver driver)
	{
		this.driver = driver;
	}

	public void pause(double seconds)
	{
		try
		{
			Thread.sleep(new Double(seconds).intValue());
		} catch (InterruptedException e)
		{
			LOGGER.error(e.getMessage());
		}
	}
}
