package com.qaprosoft.zafira.tests.gui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public abstract class BasePage extends AbstractPage
{

	protected BasePage(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public boolean waitUtilPageLoading()
	{
		By progressBarSpinner = By.id("loading-bar-spinner");
		return isElementPresent(progressBarSpinner, 4) && waitUntilElementPresent(progressBarSpinner, 20);
	}

	protected boolean isElementPresent(By by, long seconds)
	{
		boolean result;
		try
		{
			driver.manage().timeouts().implicitlyWait(0L, TimeUnit.SECONDS);
			((Wait)(new WebDriverWait(driver, seconds, 1000))).until(dr -> driver.findElement(by).isDisplayed());
			result = true;
		} catch (Exception e)
		{
			result = false;
		} finally
		{
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}
		return result;
	}

	protected boolean waitUntilElementPresent(By by, long seconds)
	{
		boolean result;
		try
		{
			driver.manage().timeouts().implicitlyWait(0L, TimeUnit.SECONDS);
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
			result = true;
		} catch (Exception e)
		{
			result = false;
		} finally
		{
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}
		return result;
	}
}
