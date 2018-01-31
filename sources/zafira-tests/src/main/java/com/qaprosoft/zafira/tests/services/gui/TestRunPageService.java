package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.WebDriver;

public class TestRunPageService extends AbstractPageService
{

	private TestRunPage testRunPage;

	public TestRunPageService(WebDriver driver)
	{
		super(driver);
		this.testRunPage = new TestRunPage(driver);
	}

	public TestRunPage getTestRunPage()
	{
		return testRunPage;
	}
}
