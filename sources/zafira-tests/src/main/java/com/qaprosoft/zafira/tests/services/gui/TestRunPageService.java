package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class TestRunPageService extends AbstractPageService
{

	private TestRunPage testRunPage;

	public TestRunPageService(WebDriver driver)
	{
		super(driver);
		this.testRunPage = new TestRunPage(driver);
	}


}
