package com.qaprosoft.zafira.services.util;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverUtil
{
	public static boolean waitForJSandJQueryToLoad(final WebDriver wd)
	{
		WebDriverWait wait = new WebDriverWait(wd, 30);
		// wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver driver)
			{
				try
				{
					return ((Long) ((JavascriptExecutor) wd).executeScript("return jQuery.active") == 0);
				} catch (Exception e)
				{
					// no jQuery present
					return true;
				}
			}
		};
		// wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver driver)
			{
				return ((JavascriptExecutor) wd).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}

	public static void pause(int time, TimeUnit tu) throws InterruptedException
	{
		Thread.sleep(tu.toMillis(time));
	}
}