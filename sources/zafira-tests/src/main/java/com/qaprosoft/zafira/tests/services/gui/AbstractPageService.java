package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.util.Config;
import org.openqa.selenium.WebDriver;

public abstract class AbstractPageService
{
	protected int GENERAL_DASHBOARD_ID = Integer.valueOf(Config.get("dashboard.general.id"));
	protected WebDriver driver;

	protected AbstractPageService(WebDriver driver)
	{
		this.driver = driver;
	}
}
