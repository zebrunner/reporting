package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.components.modals.JobViewSettingModalWindow;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestRunViewPage extends BasePage
{

	@FindBy(xpath = "//md-dialog")
	private JobViewSettingModalWindow jobViewSettingModalWindow;

	public TestRunViewPage(WebDriver driver)
	{
		super(driver, "/views");
	}

	public JobViewSettingModalWindow getJobViewSettingModalWindow()
	{
		return jobViewSettingModalWindow;
	}
}
