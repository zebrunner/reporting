package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class TestRunPageService extends AbstractPageWithTableService
{

	public static final Integer CHECKBOX_COLUMN_NUMBER = 1;
	public static final Integer NAME_COLUMN_NUMBER = 2;
	public static final Integer ENVIRONMENT_COLUMN_NUMBER = 3;
	public static final Integer PLATFORM_COLUMN_NUMBER = 4;
	public static final Integer STATISTICS_COLUMN_NUMBER = 5;
	public static final Integer DATE_COLUMN_NUMBER = 6;

	private TestRunPage testRunPage;

	public TestRunPageService(WebDriver driver)
	{
		super(driver);
		this.testRunPage = new TestRunPage(driver);
	}

	public WebElement getCheckboxByIndex(int index)
	{
		return getTableColumnByIndex(index, CHECKBOX_COLUMN_NUMBER).findElement(By.tagName("md-checkbox"));
	}

	public String getTestRunTitleByIndex(int index)
	{
		return getTableColumnByIndex(index, NAME_COLUMN_NUMBER).findElement(By.tagName("b")).getText();
	}

	public WebElement getTestRunJenkinsLinkByIndex(int index)
	{
		return getTableColumnByIndex(index, NAME_COLUMN_NUMBER).findElement(By.tagName("a"));
	}

	public String getTestRunAppVersionByIndex(int index)
	{
		return getTableColumnByIndex(index, NAME_COLUMN_NUMBER).findElement(By.tagName("small")).getText();
	}

	public String getEnvironmentByIndex(int index)
	{
		return getTableColumnByIndex(index, ENVIRONMENT_COLUMN_NUMBER).findElement(By.tagName("span")).getText();
	}

	public String getPlatformByIndex(int index)
	{
		String[] classes = getTableColumnByIndex(index, PLATFORM_COLUMN_NUMBER).findElement(By.tagName("span")).getAttribute("class").split(" ");
		return classes[classes.length - 1].toLowerCase();
	}


}
