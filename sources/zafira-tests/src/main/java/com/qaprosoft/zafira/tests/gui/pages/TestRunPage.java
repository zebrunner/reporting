package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestRunPage extends BasePage
{

	@FindBy(xpath = "//section//md-menu")
	private List<WebElement> testRunsMenuButtons;

	@FindBy(xpath = "//tbody//tr")
	private List<WebElement> testRunRows;

	public TestRunPage(WebDriver driver)
	{
		super(driver, "/tests/runs");
	}

	public List<WebElement> getTestRunsMenuButtons()
	{
		return testRunsMenuButtons;
	}

	public List<WebElement> getTestRunRows()
	{
		return testRunRows;
	}
}
