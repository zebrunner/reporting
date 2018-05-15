/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.util;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ViewportPastingDecorator;

public class WebDriverUtil
{

	private static final Long IMPLICITY_TIMEOUT = 30L;

	public static boolean waitForJSandJQueryToLoad(final WebDriver wd)
	{
		WebDriverWait wait = new WebDriverWait(wd, IMPLICITY_TIMEOUT);
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

	public static boolean waitUntilPageIsLoaded(final WebDriver wd)
	{
		boolean result;
		try
		{
			WebDriverWait wait = new WebDriverWait(wd, 120);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading-bar-spinner")));
			result = true;
		} catch (Exception e)
		{
			result = false;
		}
		return result;
	}

	public static BufferedImage takeScreenShot(final WebDriver driver, final WebElement area) {
		ViewportPastingDecorator viewportPastingDecorator = new ViewportPastingDecorator(ShootingStrategies.scaling(2)).withScrollTimeout(900);
		final Screenshot screenshot = new AShot()
				.shootingStrategy(viewportPastingDecorator)
				.coordsProvider(new WebDriverCoordsProvider())
				.takeScreenshot(driver, area);
		return screenshot.getImage();
	}

	public static boolean isPageLoading(final WebDriver wd)
	{
		return isElementPresent(wd, By.id("loading-bar-spinner"), 5);
	}

	public static boolean isPageLoadingWithAnimation(final WebDriver wd)
	{
		return isElementPresent(wd, By.xpath("//*[@id = 'loader-container' and contains(@style, 'display: none')]"), 5);
	}

	public static boolean isElementPresent(final WebDriver wd, final By by, final long timeoutOutInSeconds)
	{
		boolean result;
		try
		{
			wd.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			Wait<WebDriver> wait = new WebDriverWait(wd, timeoutOutInSeconds);
			wait.until(dr -> wd.findElement(by).isDisplayed());
			result = true;
		} catch (Exception e)
		{
			result = false;
		} finally
		{
			wd.manage().timeouts().implicitlyWait(IMPLICITY_TIMEOUT, TimeUnit.SECONDS);
		}
		return result;
	}

	public static void pause(int time, TimeUnit tu) throws InterruptedException
	{
		Thread.sleep(tu.toMillis(time));
	}
}