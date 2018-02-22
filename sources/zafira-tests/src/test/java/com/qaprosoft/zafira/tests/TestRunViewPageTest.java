package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.pages.TestRunViewPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestRunViewPageTest extends AbstractTest
{

	private TestRunViewPage testRunViewPage;

	@BeforeMethod
	public void setup()
	{
		this.testRunViewPage = new TestRunViewPage(driver);
	}

	@Test
	public void verifyCreateTestRunViewTest()
	{
		
	}
}
